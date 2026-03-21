package ru.vlsu.ispi.movieproject.dto.movie;

import lombok.Data;

@Data
public class MovieDto {
    private Long id;
    private Integer kinopoiskId;
    private String name;
    private String posterUrl;
    private String releaseYear;
    private Double userRating;
}
