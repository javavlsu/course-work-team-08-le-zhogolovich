package ru.vlsu.ispi.movieproject.service;

import ru.vlsu.ispi.movieproject.dto.movie.MovieDto;

import java.util.List;

public interface MovieService {
    List<MovieDto> getAllMovies();
}
