package de.presti.wrapper.entities.channel;

import com.google.gson.JsonObject;
import lombok.Getter;

@Getter
public class ChannelResult {

    String title;

    String description;

    String channelUrl;

    String vanityUrl;

    String avatarUrl;

    String subscriberCountText;

    boolean familySafe;

    public ChannelResult(JsonObject jsonObject) {
        subscriberCountText = jsonObject.getAsJsonObject("header")
                .getAsJsonObject("c4TabbedHeaderRenderer").getAsJsonObject("subscriberCountText")
                .getAsJsonPrimitive("simpleText").getAsString();

        JsonObject metadata = jsonObject.getAsJsonObject("metadata").getAsJsonObject("channelMetadataRenderer");

        title = metadata.getAsJsonPrimitive("title").getAsString();
        description = metadata.getAsJsonPrimitive("description").getAsString();
        channelUrl = metadata.getAsJsonPrimitive("channelUrl").getAsString();
        vanityUrl = metadata.getAsJsonPrimitive("vanityChannelUrl").getAsString();

        avatarUrl = metadata.getAsJsonObject("avatar")
                .getAsJsonArray("thumbnails").get(0).getAsJsonObject().getAsJsonPrimitive("url").getAsString();

        familySafe = metadata.getAsJsonPrimitive("isFamilySafe").getAsBoolean();
    }

    public long getSubscriber() {
        return Long.parseLong(subscriberCountText.replaceAll("[^0-9]", ""));
    }

}
