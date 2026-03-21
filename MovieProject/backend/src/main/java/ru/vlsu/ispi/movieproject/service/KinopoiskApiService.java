package ru.vlsu.ispi.movieproject.service;

import ru.vlsu.ispi.movieproject.dto.imports.ExternalSourcesResponseDto;
import ru.vlsu.ispi.movieproject.dto.imports.FiltersResponseDto;
import ru.vlsu.ispi.movieproject.dto.movie.MovieDetailsDto;
import ru.vlsu.ispi.movieproject.dto.imports.MovieListResponseDto;

public interface KinopoiskApiService {
    MovieListResponseDto getMovieList(int page);
    MovieDetailsDto getMovieDetails(Integer kinopoiskId);
    FiltersResponseDto getFilters();
    ExternalSourcesResponseDto getExternalSources(Integer kinopoiskId);
}
