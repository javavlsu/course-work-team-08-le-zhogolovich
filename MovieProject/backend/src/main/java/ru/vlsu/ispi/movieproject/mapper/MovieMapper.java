package ru.vlsu.ispi.movieproject.mapper;

import org.springframework.stereotype.Component;
import ru.vlsu.ispi.movieproject.dto.movie.MovieDto;
import ru.vlsu.ispi.movieproject.model.Country;
import ru.vlsu.ispi.movieproject.model.Genre;
import ru.vlsu.ispi.movieproject.model.Movie;
import ru.vlsu.ispi.movieproject.model.Tag;

import java.util.stream.Collectors;

@Component
public class MovieMapper {
    public static MovieDto toDto(Movie movie) {
        return new MovieDto(
                movie.getId(),
                movie.getKinopoiskId(),
                movie.getName(),
                movie.getOriginalName(),
                movie.getReleaseYear(),
                movie.getPosterUrl(),
                movie.getOverview(),
                movie.getGenres().stream()
                        .map(Genre::getName)
                        .collect(Collectors.toSet()),
                movie.getCountries().stream()
                        .map(Country::getName)
                        .collect(Collectors.toSet()),
                movie.getTags().stream()
                        .map(Tag::getName)
                        .collect(Collectors.toSet())
                );
    }
}
