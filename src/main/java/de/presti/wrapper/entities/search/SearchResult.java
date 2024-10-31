package de.presti.wrapper.entities.search;

import com.google.gson.JsonObject;
import lombok.*;

/**
 * Represents a search result.
 */
@Getter
@ToString(exclude = "internalObject")
@NoArgsConstructor
@AllArgsConstructor
public class SearchResult {

    /**
     * The id of the search result.
     */
    String id;

    /**
     * The title of the search result.
     */
    String title;

    /**
     * The owner id of the search result.
     */
    String ownerId;

    /**
     * The description of the search result.
     */
    JsonObject internalObject;

    /**
     * Creates a new search result.
     * @param jsonObject The json object.
     */
    public SearchResult(JsonObject jsonObject) {
        internalObject = jsonObject;
    }
}
