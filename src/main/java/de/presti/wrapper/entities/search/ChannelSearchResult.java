package de.presti.wrapper.entities.search;

import com.google.gson.JsonObject;
import de.presti.wrapper.utils.NumberUtil;
import de.presti.wrapper.utils.ParserUtil;
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

        subscriberCountText = ParserUtil.extractSimpleText(jsonObject, "videoCountText");
    }

    /**
     * Gets the subscriber count.
     * @return The subscriber count.
     */
    public long getSubscriber() {
        return NumberUtil.extractLong(subscriberCountText);
    }

}
