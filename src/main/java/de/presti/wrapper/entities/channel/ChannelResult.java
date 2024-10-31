package de.presti.wrapper.entities.channel;

import com.google.gson.JsonObject;
import de.presti.wrapper.utils.NumberUtil;
import de.presti.wrapper.utils.ParserUtil;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Represents a channel result.
 */
@Slf4j
@Getter
@ToString(exclude = "internalObject")
public class ChannelResult {

    /**
     * The id of the channel.
     */
    String id;

    /**
     * The title of the channel.
     */
    String title;

    /**
     * The description of the channel.
     */
    String description;

    /**
     * The url of the channel.
     */
    String channelUrl;

    /**
     * The vanity url of the channel.
     */
    String vanityUrl;

    /**
     * The rss url of the channel.
     */
    String rssUrl;

    /**
     * The avatar url of the channel.
     */
    String avatarUrl;

    /**
     * The subscriber count text of the channel.
     */
    String subscriberCountText;

    /**
     * If the channel is family safe.
     */
    boolean familySafe;

    /**
     * The internal json object.
     */
    JsonObject internalObject;

    /**
     * Creates a new channel result.
     *
     * @param jsonObject The json object.
     */
    public ChannelResult(JsonObject jsonObject) {
        internalObject = jsonObject;

        if (internalObject.has("success") && !internalObject.getAsJsonPrimitive("success").getAsBoolean()) {
            log.error("Couldn't get video info! Reason: Failed sending a request");
            return;
        }

        if (jsonObject.has("header") && jsonObject.getAsJsonObject("header").has("c4TabbedHeaderRenderer")) {
            // Apparently YouTube sometimes does not send the c4TabbedHeaderRenderer, so we need to check for it.
            subscriberCountText = ParserUtil.extractSimpleText(jsonObject.getAsJsonObject("header")
                    .getAsJsonObject("c4TabbedHeaderRenderer"), "subscriberCountText");
        }

        JsonObject metadata = jsonObject.getAsJsonObject("metadata").getAsJsonObject("channelMetadataRenderer");

        try {
            id = metadata.getAsJsonPrimitive("externalId").getAsString();

            title = metadata.getAsJsonPrimitive("title").getAsString();
            description = metadata.getAsJsonPrimitive("description").getAsString();
            channelUrl = metadata.getAsJsonPrimitive("channelUrl").getAsString();
            vanityUrl = metadata.getAsJsonPrimitive("vanityChannelUrl").getAsString();

            rssUrl = metadata.getAsJsonPrimitive("rssUrl").getAsString();

            avatarUrl = ParserUtil.extractSimpleText(metadata.getAsJsonObject("avatar"), "thumbnails");

            familySafe = metadata.getAsJsonPrimitive("isFamilySafe").getAsBoolean();
        } catch (Exception exception) {
            log.info(metadata.toString());
            throw exception;
        }
    }

    /**
     * Gets the subscriber count.
     *
     * @return The subscriber count.
     */
    public long getSubscriber() {
        if (subscriberCountText == null) return 0;
        return NumberUtil.extractLong(subscriberCountText);
    }

}
