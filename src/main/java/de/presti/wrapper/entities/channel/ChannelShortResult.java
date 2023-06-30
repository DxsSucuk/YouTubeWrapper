package de.presti.wrapper.entities.channel;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.presti.wrapper.entities.VideoResult;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a channel result with shorts.
 */
@Getter
@ToString
public class ChannelShortResult extends ChannelResult {

    /**
     * The shorts of the channel.
     */
    List<VideoResult> shorts = new ArrayList<>();

    /**
     * Creates a new channel short result.
     * @param jsonObject The json object.
     */
    public ChannelShortResult(JsonObject jsonObject) {
        super(jsonObject);

        JsonArray tabs = jsonObject.getAsJsonObject("contents").getAsJsonObject("twoColumnBrowseResultsRenderer").getAsJsonArray("tabs");

        JsonArray shortContent = null;

        for (int i = 0; i < tabs.size(); i++) {
            JsonObject currentTabObject = tabs.get(i)
                    .getAsJsonObject()
                    .getAsJsonObject("tabRenderer");

            if (currentTabObject.getAsJsonPrimitive("title").getAsString().equalsIgnoreCase("Shorts")) {
                shortContent = currentTabObject.getAsJsonObject("content")
                        .getAsJsonObject("richGridRenderer").getAsJsonArray("contents");
                break;
            }
        }

        if (shortContent == null) {
            throw new IllegalStateException("No short content found!");
        }

        for (int i = 0; i < shortContent.size(); i++) {
            JsonObject currentVideoObject = shortContent.get(i).getAsJsonObject();
            if (currentVideoObject == null) continue;

            if (currentVideoObject.has("richItemRenderer")) {
                currentVideoObject = currentVideoObject
                        .getAsJsonObject("richItemRenderer");
                if (currentVideoObject.has("content")) {
                    currentVideoObject = currentVideoObject
                            .getAsJsonObject("content")
                            .getAsJsonObject("reelItemRenderer");
                    shorts.add(new VideoResult(currentVideoObject, true, true));
                }
            }
        }
    }
}
