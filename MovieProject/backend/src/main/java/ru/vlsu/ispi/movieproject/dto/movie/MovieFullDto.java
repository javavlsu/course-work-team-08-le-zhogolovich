package ru.vlsu.ispi.movieproject.dto.movie;

import lombok.Data;
import ru.vlsu.ispi.movieproject.dto.imports.ExternalSourceDto;

import java.util.Set;

@Data
public class MovieFullDto {
    private Long id;
    private Integer kinopoiskId;
    private String name;
    private String nameOriginal;
    private Integer releaseYear;
    private String posterUrl;
    private String overview;
    private Double ratingKinopoisk;
    private Double ratingImdb;
    private Double avgRating;
    private Integer ratingsCount;
    private Double myRating;
    private Set<String> genres;
    private Set<String> countries;
    private Set<String> tags;
    private Set<ExternalSourceDto> externalSources;
}
