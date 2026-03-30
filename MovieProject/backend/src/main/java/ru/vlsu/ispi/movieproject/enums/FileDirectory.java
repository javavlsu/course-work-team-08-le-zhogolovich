package ru.vlsu.ispi.movieproject.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FileDirectory {
    AVATARS("avatars"),
    COMPILATIONS("compilations"),
    REVIEWS("reviews");

    private final String folder;
}
