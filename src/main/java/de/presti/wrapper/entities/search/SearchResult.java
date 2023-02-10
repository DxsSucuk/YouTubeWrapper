package de.presti.wrapper.entities.search;

import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SearchResult {

    String id;

    String title;

    String ownerId;

    JsonObject internalObject;
    public SearchResult(JsonObject jsonObject) {
        internalObject = jsonObject;
    }

    public enum FILTER {

        CHANNEL("EgIQAg%3D%3D"),
        VIDEO("EgIQAQ%3D%3D"),
        NONE("");

        @Getter(AccessLevel.PUBLIC)
        String params;

        FILTER(String params) {
            this.params = params;
        }

    }
}
