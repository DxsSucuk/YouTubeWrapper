package de.presti.wrapper.entities.search;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * Filters for the search.
 */
public enum SearchFilter {

    /**
     * Only return channels.
     */
    CHANNEL("EgIQAg%3D%3D"),

    /**
     * Only return videos.
     */
    VIDEO("EgIQAQ%3D%3D"),

    /**
     * Return everything.
     */
    NONE("");

    /**
     * The params for the filter.
     */
    @Getter(AccessLevel.PUBLIC)
    String params;

    /**
     * Creates a new filter.
     * @param params The params for the filter.
     */
    SearchFilter(String params) {
        this.params = params;
    }

}