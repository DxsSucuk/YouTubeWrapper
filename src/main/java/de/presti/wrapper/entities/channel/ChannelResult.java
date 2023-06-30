package de.presti.wrapper.entities.channel;

import com.google.gson.JsonObject;
import de.presti.wrapper.utils.NumberUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class ChannelResult {

    String id;

    String title;

    String description;

    String channelUrl;

    String vanityUrl;

    String rssUrl;

    String avatarUrl;

    String subscriberCountText;

    boolean familySafe;

    JsonObject internalObject;
    
    public ChannelResult(JsonObject jsonObject) {
        internalObject = jsonObject;

        if (internalObject.has("failed") && internalObject.getAsJsonPrimitive("failed").getAsBoolean()) {
            log.error("Couldn't get video info! Reason: Failed sending a request");
            return;
        }

        subscriberCountText = jsonObject.getAsJsonObject("header")
                .getAsJsonObject("c4TabbedHeaderRenderer").getAsJsonObject("subscriberCountText")
                .getAsJsonPrimitive("simpleText").getAsString();

        JsonObject metadata = jsonObject.getAsJsonObject("metadata").getAsJsonObject("channelMetadataRenderer");

        id = metadata.getAsJsonPrimitive("externalId").getAsString();

        title = metadata.getAsJsonPrimitive("title").getAsString();
        description = metadata.getAsJsonPrimitive("description").getAsString();
        channelUrl = metadata.getAsJsonPrimitive("channelUrl").getAsString();
        vanityUrl = metadata.getAsJsonPrimitive("vanityChannelUrl").getAsString();

        rssUrl = metadata.getAsJsonPrimitive("rssUrl").getAsString();

        avatarUrl = metadata.getAsJsonObject("avatar")
                .getAsJsonArray("thumbnails").get(0).getAsJsonObject().getAsJsonPrimitive("url").getAsString();

        familySafe = metadata.getAsJsonPrimitive("isFamilySafe").getAsBoolean();
    }

    public long getSubscriber() {
        return NumberUtil.extractLong(subscriberCountText);
    }

}
