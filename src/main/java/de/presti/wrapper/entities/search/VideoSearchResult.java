package de.presti.wrapper.entities.search;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter(AccessLevel.PUBLIC)
@AllArgsConstructor
public class VideoSearchResult extends SearchResult {

    String videoId;

    String title;

    long viewCount;

}
