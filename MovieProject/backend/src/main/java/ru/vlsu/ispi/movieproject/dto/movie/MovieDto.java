package ru.vlsu.ispi.movieproject.dto.movie;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieDto {
    private Long id;
    private Integer kinopoiskId;
    private String name;
    private String originalName;
    private Integer releaseYear;
    private String posterUrl;
    private String overview;
    private Set<String> genres;
    private Set<String> countries;
    private Set<String> tags;
}