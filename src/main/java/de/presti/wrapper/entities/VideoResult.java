package de.presti.wrapper.entities;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;

// TODO:: find a way to get the upload date in a nice format.

@Getter
public class VideoResult {

    String id;
    String title;
    String thumbnail;
    String durationText;
    String viewCountText;
    String descriptionSnippet;

    public VideoResult(JsonObject jsonObject) {
        id = jsonObject.getAsJsonPrimitive("videoId").getAsString();
        JsonArray thumbnailArray = jsonObject.getAsJsonObject("thumbnail").getAsJsonArray("thumbnails");
        thumbnail = thumbnailArray.get(thumbnailArray.size() - 1).getAsJsonObject()
                .getAsJsonPrimitive("url").getAsString();
        title = jsonObject.getAsJsonObject("title").getAsJsonArray("runs").get(0).getAsJsonObject()
                .getAsJsonPrimitive("text").getAsString();
        durationText = jsonObject.getAsJsonObject("lengthText").getAsJsonPrimitive("simpleText").getAsString();
        viewCountText = jsonObject.getAsJsonObject("viewCountText").getAsJsonPrimitive("simpleText").getAsString();

        if (jsonObject.has("descriptionSnippet")) {
            descriptionSnippet = jsonObject.getAsJsonObject("descriptionSnippet").getAsJsonArray("runs").get(0).getAsJsonObject()
                    .getAsJsonPrimitive("text").getAsString();
        }
    }

    public long getViews() {
        return Long.parseLong(viewCountText.replaceAll("[^0-9]", ""));
    }
}
