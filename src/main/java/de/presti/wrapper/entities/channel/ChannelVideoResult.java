package de.presti.wrapper.entities.channel;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.presti.wrapper.entities.VideoResult;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a channel result with videos.
 */
@Getter
@ToString
public class ChannelVideoResult extends ChannelResult {

    /**
     * The videos of the channel.
     */
    List<VideoResult> videos = new ArrayList<>();

    /**
     * Creates a new channel video result.
     * @param jsonObject The json object.
     */
    public ChannelVideoResult(JsonObject jsonObject) {
        super(jsonObject);

        JsonArray tabs = jsonObject.getAsJsonObject("contents").getAsJsonObject("twoColumnBrowseResultsRenderer").getAsJsonArray("tabs");

        JsonArray videoContent = null;

        for (int i = 0; i < tabs.size(); i++) {
            JsonObject currentTabObject = tabs.get(i)
                    .getAsJsonObject();

            if (currentTabObject == null) continue;

            if (!currentTabObject.has("tabRenderer")) continue;

            currentTabObject = currentTabObject
                    .getAsJsonObject("tabRenderer");

            if (currentTabObject.getAsJsonPrimitive("title").getAsString().equalsIgnoreCase("Videos")) {
                videoContent = currentTabObject.getAsJsonObject("content")
                        .getAsJsonObject("richGridRenderer").getAsJsonArray("contents");
                break;
            }
        }

        if (videoContent == null) {
            throw new IllegalStateException("No video content found!");
        }

        for (int i = 0; i < videoContent.size(); i++) {
            JsonObject currentVideoObject = videoContent.get(i).getAsJsonObject();
            if (currentVideoObject == null) continue;

            if (currentVideoObject.has("richItemRenderer")) {
                currentVideoObject = currentVideoObject
                        .getAsJsonObject("richItemRenderer");
                if (currentVideoObject.has("content")) {
                    currentVideoObject = currentVideoObject
                            .getAsJsonObject("content")
                            .getAsJsonObject("videoRenderer");
                    videos.add(new VideoResult(currentVideoObject, true, false));
                }
            }
        }
    }
}
