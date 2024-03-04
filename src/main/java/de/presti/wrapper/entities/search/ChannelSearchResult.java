package de.presti.wrapper.entities.search;

import com.google.gson.JsonObject;
import de.presti.wrapper.utils.NumberUtil;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * Represents a channel search result.
 */
@ToString
@Getter(AccessLevel.PUBLIC)
@AllArgsConstructor
public class ChannelSearchResult extends SearchResult {

    /**
     * The subscriber count text from the channel.
     */
    String subscriberCountText;

    /**
     * Creates a new channel search result.
     * @param jsonObject The json object.
     */
    public ChannelSearchResult(JsonObject jsonObject) {
        super(jsonObject);

        if (internalObject.has("success") && !internalObject.getAsJsonPrimitive("success").getAsBoolean()) {
            return;
        }

        title = jsonObject.getAsJsonObject("title").getAsJsonPrimitive("simpleText").getAsString();
        id = jsonObject.getAsJsonPrimitive("channelId").getAsString();
        ownerId = id;

        if (jsonObject.has("videoCountText")) {
            JsonObject videoCountText = jsonObject.getAsJsonObject("videoCountText");
            if (videoCountText.has("runs")) {
                JsonObject runs = videoCountText.getAsJsonArray("runs").get(0).getAsJsonObject();
                if (runs.has("text")) {
                    subscriberCountText = runs.getAsJsonPrimitive("text").getAsString();
                }
            }

            if (videoCountText.has("simpleText")) {
                subscriberCountText = videoCountText.getAsJsonPrimitive("simpleText").getAsString();
            }
        }
    }

    /**
     * Gets the subscriber count.
     * @return The subscriber count.
     */
    public long getSubscriber() {
        return NumberUtil.extractLong(subscriberCountText);
    }

}
