package de.presti.wrapper.entities;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;

// TODO:: find a way to get the upload date in a nice format.

@Getter
public class VideoResult {

    String id;
    String ownerId;
    String ownerName;
    String title;
    String thumbnail;
    String durationText;
    String viewCountText;
    String descriptionSnippet;
    boolean live;
    long lengthSeconds;
    long uploadDate = -1;

    public VideoResult(JsonObject jsonObject, boolean importFromChannel) {
        if (importFromChannel) {
            id = jsonObject.getAsJsonPrimitive("videoId").getAsString();
            JsonArray thumbnailArray = jsonObject.getAsJsonObject("thumbnail").getAsJsonArray("thumbnails");
            thumbnail = thumbnailArray.get(thumbnailArray.size() - 1).getAsJsonObject()
                    .getAsJsonPrimitive("url").getAsString();
            title = jsonObject.getAsJsonObject("title").getAsJsonArray("runs").get(0).getAsJsonObject()
                    .getAsJsonPrimitive("text").getAsString();
            durationText = jsonObject.getAsJsonObject("lengthText").getAsJsonPrimitive("simpleText").getAsString();
            viewCountText = jsonObject.getAsJsonObject("viewCountText").getAsJsonPrimitive("simpleText").getAsString();

            if (jsonObject.has("descriptionSnippet")) {
                descriptionSnippet = jsonObject.getAsJsonObject("descriptionSnippet").getAsJsonArray("runs").get(0).getAsJsonObject()
                        .getAsJsonPrimitive("text").getAsString();
            }
        } else {
            uploadDate = jsonObject.getAsJsonObject("streamingData").getAsJsonArray("formats").get(0)
                    .getAsJsonObject().getAsJsonPrimitive("lastModified").getAsLong() / 1000;
            JsonObject videoDetails = jsonObject.getAsJsonObject("videoDetails");
            ownerId = videoDetails.getAsJsonPrimitive("channelId").getAsString();
            ownerName = videoDetails.getAsJsonPrimitive("author").getAsString();
            id = videoDetails.getAsJsonPrimitive("videoId").getAsString();
            title = videoDetails.getAsJsonPrimitive("title").getAsString();

            JsonArray thumbnailArray = videoDetails.getAsJsonObject("thumbnail").getAsJsonArray("thumbnails");
            thumbnail = thumbnailArray.get(thumbnailArray.size() - 1).getAsJsonObject()
                    .getAsJsonPrimitive("url").getAsString();

            viewCountText = videoDetails.getAsJsonPrimitive("viewCount").getAsString();
            lengthSeconds = videoDetails.getAsJsonPrimitive("lengthSeconds").getAsLong();
            descriptionSnippet = videoDetails.getAsJsonPrimitive("shortDescription").getAsString();
            live = videoDetails.getAsJsonPrimitive("isLiveContent").getAsBoolean();
        }
    }

    public long getViews() {
        return Long.parseLong(viewCountText.replaceAll("[^0-9]", ""));
    }
}
