package de.presti.wrapper.entities.search;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class SearchResult {

    @SerializedName(value = "channelId", alternate = "videoId")
    String id;
}
