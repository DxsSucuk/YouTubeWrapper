package de.presti.wrapper.entities.channel;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.presti.wrapper.entities.VideoResult;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ChannelVideoResult extends ChannelResult {

    List<VideoResult> videos = new ArrayList<>();

    public ChannelVideoResult(JsonObject jsonObject) {
        super(jsonObject);

        JsonArray tabs = jsonObject.getAsJsonObject("contents").getAsJsonObject("twoColumnBrowseResultsRenderer").getAsJsonArray("tabs");

        JsonArray videoContent = null;

        for (int i = 0; i < tabs.size(); i++) {
            JsonObject currentTabObject = tabs.get(i)
                    .getAsJsonObject()
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
                    videos.add(new VideoResult(currentVideoObject, true));
                }
            }
        }
    }
}
