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

    public SearchResult(JsonObject jsonObject) {
    }

    enum FILTER {

        CHANNEL("EgIQAg%3D%3D"),
        VIDEO("EgIQAQ%3D%3D");

        @Getter(AccessLevel.PUBLIC)
        String params;

        FILTER(String params) {
            this.params = params;
        }

    }
}
