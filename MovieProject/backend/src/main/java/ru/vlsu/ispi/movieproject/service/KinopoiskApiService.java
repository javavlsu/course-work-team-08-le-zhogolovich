package ru.vlsu.ispi.movieproject.service;

import ru.vlsu.ispi.movieproject.dto.imports.FiltersResponseDto;
import ru.vlsu.ispi.movieproject.dto.movie.MovieDetailsDto;
import ru.vlsu.ispi.movieproject.dto.movie.MovieListResponseDto;

public interface KinopoiskApiService {
    public MovieListResponseDto getMovieList();
    public MovieDetailsDto getMovieDetails();
    public FiltersResponseDto getFilters();
}
