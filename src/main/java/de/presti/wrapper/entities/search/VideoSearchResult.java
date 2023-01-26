package de.presti.wrapper.entities.search;

import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter(AccessLevel.PUBLIC)
@AllArgsConstructor
public class VideoSearchResult extends SearchResult {

    String viewCountText;

    public VideoSearchResult(JsonObject jsonObject) {
        super(jsonObject);
        title = jsonObject.getAsJsonObject("title").getAsJsonArray("runs").get(0).getAsJsonObject()
                .getAsJsonPrimitive("text").getAsString();

        id = jsonObject.getAsJsonPrimitive("videoId").getAsString();

        ownerId = jsonObject.getAsJsonObject("ownerText").getAsJsonArray("runs").get(0).getAsJsonObject()
                .getAsJsonObject("navigationEndpoint").getAsJsonObject("browseEndpoint")
                .getAsJsonPrimitive("browseId").getAsString();

        viewCountText = jsonObject.getAsJsonObject("viewCountText").getAsJsonPrimitive("simpleText").getAsString();
    }

    public long getViews() {
        return Long.parseLong(viewCountText.replaceAll("[^0-9]", ""));
    }

}
