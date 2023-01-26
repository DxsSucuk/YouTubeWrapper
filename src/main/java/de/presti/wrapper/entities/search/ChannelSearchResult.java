package de.presti.wrapper.entities.search;

import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter(AccessLevel.PUBLIC)
@AllArgsConstructor
public class ChannelSearchResult extends SearchResult {

    String subscriberCountText;

    public ChannelSearchResult(JsonObject jsonObject) {
        super(jsonObject);
        title = jsonObject.getAsJsonObject("title").getAsJsonPrimitive("simpleText").getAsString();
        id = jsonObject.getAsJsonPrimitive("channelId").getAsString();
        ownerId = id;
        subscriberCountText = jsonObject.getAsJsonObject("videoCountText").getAsJsonPrimitive("simpleText").getAsString();
    }

    public long getSubscriber() {
        return Long.parseLong(subscriberCountText.replaceAll("[^0-9]", ""));
    }

}
