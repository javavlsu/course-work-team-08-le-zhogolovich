package ru.vlsu.ispi.movieproject.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.vlsu.ispi.movieproject.dto.movie.MovieDto;
import ru.vlsu.ispi.movieproject.dto.movie.MovieFullDto;
import ru.vlsu.ispi.movieproject.model.Movie;

public interface MovieService {
    Page<MovieDto> getAllMovies(Pageable pageable);
    MovieFullDto getMovie(Long id);
    void enrichMovie(Movie movie);
    void loadExternalSources(Movie movie);
}
