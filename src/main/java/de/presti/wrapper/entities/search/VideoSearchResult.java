package de.presti.wrapper.entities.search;

import com.google.gson.JsonObject;
import de.presti.wrapper.utils.NumberUtil;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * Represents a video search result.
 */
@ToString
@Getter(AccessLevel.PUBLIC)
@AllArgsConstructor
public class VideoSearchResult extends SearchResult {

    /**
     * The view count text from the video.
     */
    String viewCountText;

    /**
     * Creates a new video search result.
     * @param jsonObject The json object.
     */
    public VideoSearchResult(JsonObject jsonObject) {
        super(jsonObject);
        title = jsonObject.getAsJsonObject("title").getAsJsonArray("runs").get(0).getAsJsonObject()
                .getAsJsonPrimitive("text").getAsString();

        id = jsonObject.getAsJsonPrimitive("videoId").getAsString();

        ownerId = jsonObject.getAsJsonObject("ownerText").getAsJsonArray("runs").get(0).getAsJsonObject()
                .getAsJsonObject("navigationEndpoint").getAsJsonObject("browseEndpoint")
                .getAsJsonPrimitive("browseId").getAsString();
        
        if (jsonObject.has("viewCountText") && jsonObject.getAsJsonObject("viewCountText").has("simpleText")) {
            viewCountText = jsonObject.getAsJsonObject("viewCountText").getAsJsonPrimitive("simpleText").getAsString();
        }
    }

    /**
     * Gets the view count.
     * @return The view count.
     */
    public long getViews() {
        return NumberUtil.extractLong(viewCountText);
    }

}
