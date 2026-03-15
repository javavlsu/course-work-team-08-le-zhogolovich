package ru.vlsu.ispi.movieproject.dto.movie;

import lombok.Data;

import java.util.Set;

@Data
public class MovieDto {
    private Integer kinopoiskId;
    private String name;
    private String nameOriginal;
    private Integer year;
    private String posterUrl;
    private Set<GenreDto> genres;
    private Set<CountryDto> countries;
}