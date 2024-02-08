package de.presti.wrapper.entities;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.presti.wrapper.utils.NumberUtil;
import io.sentry.Sentry;
import io.sentry.SentryEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

// TODO:: find a way to get the upload date in a nice format.

/**
 * Represents a video result.
 */
@Getter
@Slf4j
@ToString(exclude = "internalObject")
public class VideoResult {

    /**
     * The id of the video.
     */
    String id;

    /**
     * The id of the owner of the video.
     */
    String ownerId;

    /**
     * The name of the owner of the video.
     */
    String ownerName;

    /**
     * The title of the video.
     */
    String title;

    /**
     * The thumbnail of the video.
     */
    String thumbnail;

    /**
     * The duration of the video.
     */
    String durationText;

    /**
     * The view count text from the video.
     */
    String viewCountText;

    /**
     * The description of the video.
     */
    String descriptionSnippet;

    /**
     * If the video is live.
     */
    boolean live;

    /**
     * If the video is premiering.
     */
    boolean isPremier;

    /**
     * The length of the video in seconds.
     */
    long lengthSeconds;

    /**
     * The upload date of the video.
     */
    long uploadDate = -1;

    /**
     * The actual upload date of the video.
     */
    Date actualUploadDate;

    /**
     * The internal json object.
     */
    JsonObject internalObject;

    /**
     * The time ago of the video (based on YT API response, in milliseconds).
     */
    @Setter
    long timeAgo = -1;

    /**
     * Creates a new video result.
     * @param jsonObject The json object.
     * @param importFromChannel If the video result should be imported from a channel.
     * @param importFromShort If the video result should be imported from a short.
     */
    public VideoResult(JsonObject jsonObject, boolean importFromChannel, boolean importFromShort) {
        internalObject = jsonObject;

        if (internalObject.has("failed") && internalObject.getAsJsonPrimitive("failed").getAsBoolean()) {
            log.error("Couldn't get video info! Reason: Failed sending a request");
            return;
        }

        if (jsonObject.has("playabilityStatus")) {
            JsonObject playabilityStatus = jsonObject.getAsJsonObject("playabilityStatus");
            if (playabilityStatus.has("status")) {
                String status = playabilityStatus.getAsJsonPrimitive("status").getAsString();
                if (status.equalsIgnoreCase("error")) {
                    String errorCode = playabilityStatus.getAsJsonPrimitive("reason").getAsString();
                    log.error("Couldn't get video info! Reason: " + errorCode);
                    return;
                } else if (status.equalsIgnoreCase("live_stream_offline")) {
                    return;
                }
            }
        }

        isPremier = jsonObject.has("upcomingEventData");

        try {
            if (importFromChannel) {
                id = jsonObject.getAsJsonPrimitive("videoId").getAsString();
                JsonArray thumbnailArray = jsonObject.getAsJsonObject("thumbnail").getAsJsonArray("thumbnails");
                thumbnail = thumbnailArray.get(thumbnailArray.size() - 1).getAsJsonObject()
                        .getAsJsonPrimitive("url").getAsString();

                if (isPremier) {
                    viewCountText = jsonObject.getAsJsonObject("viewCountText").getAsJsonArray("runs").get(0).getAsJsonObject().getAsJsonPrimitive("text").getAsString();
                } else {
                    viewCountText = jsonObject.getAsJsonObject("viewCountText").getAsJsonPrimitive("simpleText").getAsString();
                }

                if (importFromShort) {
                    title = jsonObject.getAsJsonObject("headline").getAsJsonPrimitive("simpleText").getAsString();
                } else {
                    title = jsonObject.getAsJsonObject("title").getAsJsonArray("runs").get(0).getAsJsonObject()
                            .getAsJsonPrimitive("text").getAsString();
                    durationText = jsonObject.getAsJsonObject("lengthText").getAsJsonPrimitive("simpleText").getAsString();

                    if (jsonObject.has("descriptionSnippet")) {
                        descriptionSnippet = jsonObject.getAsJsonObject("descriptionSnippet").getAsJsonArray("runs").get(0).getAsJsonObject()
                                .getAsJsonPrimitive("text").getAsString();
                    }
                }
            } else {
                JsonArray formats = jsonObject.getAsJsonObject("streamingData").getAsJsonArray("formats");

                if (formats != null)
                    uploadDate = formats.get(formats.size() - 1)
                            .getAsJsonObject().getAsJsonPrimitive("lastModified").getAsLong() / 1000;

                try {
                    actualUploadDate = new SimpleDateFormat("yyyy-MM-dd").parse(jsonObject.getAsJsonObject("microformat")
                            .getAsJsonObject("playerMicroformatRenderer").getAsJsonPrimitive("uploadDate").getAsString());
                } catch (ParseException ignore) {
                }

                JsonObject videoDetails = jsonObject.getAsJsonObject("videoDetails");
                ownerId = videoDetails.getAsJsonPrimitive("channelId").getAsString();
                ownerName = videoDetails.getAsJsonPrimitive("author").getAsString();
                id = videoDetails.getAsJsonPrimitive("videoId").getAsString();
                title = videoDetails.getAsJsonPrimitive("title").getAsString();

                JsonArray thumbnailArray = videoDetails.getAsJsonObject("thumbnail").getAsJsonArray("thumbnails");
                thumbnail = thumbnailArray.get(thumbnailArray.size() - 1).getAsJsonObject()
                        .getAsJsonPrimitive("url").getAsString();

                if (videoDetails.has("viewCount"))
                    viewCountText = videoDetails.getAsJsonPrimitive("viewCount").getAsString();

                lengthSeconds = videoDetails.getAsJsonPrimitive("lengthSeconds").getAsLong();
                descriptionSnippet = videoDetails.getAsJsonPrimitive("shortDescription").getAsString();
                live = videoDetails.getAsJsonPrimitive("isLiveContent").getAsBoolean();
            }
        } catch (Exception exception) {
            if (Sentry.isEnabled()) {
                SentryEvent event = new SentryEvent(exception);
                event.setExtra("typ", "Video");
                event.setExtra("internalObject", internalObject.toString());
                Sentry.captureEvent(event);
            } else {
                throw new NullPointerException(exception.getMessage() + "\nInternal Object: " + internalObject);
            }
        }
    }

    /**
     * Gets the view count.
     * @return The view count.
     */
    public long getViews() {
        return NumberUtil.extractLong(viewCountText);
    }
}
