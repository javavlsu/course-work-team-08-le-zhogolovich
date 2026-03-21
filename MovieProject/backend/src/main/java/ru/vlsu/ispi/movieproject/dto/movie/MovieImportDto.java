package ru.vlsu.ispi.movieproject.dto.movie;

import lombok.Data;

import java.util.Set;

@Data
public class MovieImportDto {
    private Integer kinopoiskId;
    private String nameRu;
    private String nameOriginal;
    private Integer year;
    private String posterUrl;
    private Double ratingKinopoisk;
    private Double ratingImdb;
    private Set<GenreDto> genres;
    private Set<CountryDto> countries;
}