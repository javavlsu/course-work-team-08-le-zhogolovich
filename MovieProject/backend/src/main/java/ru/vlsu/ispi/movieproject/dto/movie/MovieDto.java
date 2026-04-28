package ru.vlsu.ispi.movieproject.dto.movie;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieDto {
    private Long id;
    private Integer kinopoiskId;
    private String name;
    private String posterUrl;
    private Integer releaseYear;
    private Double avgRating;
    private Double ratingKinopoisk;
    private Double ratingImdb;
}
