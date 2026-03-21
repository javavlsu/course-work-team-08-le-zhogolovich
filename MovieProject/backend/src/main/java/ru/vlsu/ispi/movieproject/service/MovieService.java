package ru.vlsu.ispi.movieproject.service;

import ru.vlsu.ispi.movieproject.dto.imports.ExternalSourcesResponseDto;
import ru.vlsu.ispi.movieproject.dto.movie.MovieFullDto;
import ru.vlsu.ispi.movieproject.dto.movie.MovieImportDto;
import ru.vlsu.ispi.movieproject.model.Movie;
import ru.vlsu.ispi.movieproject.security.CustomUserDetails;

import java.util.List;

public interface MovieService {
    List<MovieImportDto> getAllMovies();
    MovieFullDto getMovie(Long id, Long userId);
    void enrichMovie(Movie movie);
    void loadExternalSources(Movie movie);
}
