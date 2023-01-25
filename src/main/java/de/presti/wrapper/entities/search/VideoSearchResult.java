package de.presti.wrapper.entities.search;

import com.google.gson.annotations.SerializedName;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter(AccessLevel.PUBLIC)
@AllArgsConstructor
public class VideoSearchResult extends SearchResult {

    @SerializedName(value = "ownerText.runs[0].browseEndpoint.browseId")
    String ownerId;

    @SerializedName(value = "title.runs[0].text")
    String title;

    @SerializedName(value = "viewCountText.simpleText")
    String viewCountText;

    public long getViews() {
        return Long.parseLong(viewCountText.replaceAll("[^0-9]", ""));
    }

}
