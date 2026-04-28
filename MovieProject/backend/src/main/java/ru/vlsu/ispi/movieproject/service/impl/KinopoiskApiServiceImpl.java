package ru.vlsu.ispi.movieproject.service.impl;

import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import ru.vlsu.ispi.movieproject.client.KinopoiskClient;
import ru.vlsu.ispi.movieproject.dto.imports.ExternalSourcesResponseDto;
import ru.vlsu.ispi.movieproject.dto.imports.FiltersResponseDto;
import ru.vlsu.ispi.movieproject.dto.movie.MovieDetailsDto;
import ru.vlsu.ispi.movieproject.dto.imports.MovieListResponseDto;
import ru.vlsu.ispi.movieproject.exception.KinopoiskApiException;
import ru.vlsu.ispi.movieproject.service.KinopoiskApiService;

@Service
@RequiredArgsConstructor
public class KinopoiskApiServiceImpl implements KinopoiskApiService {
    private final KinopoiskClient kinopoiskClient;

    @Override
    public MovieListResponseDto getMovieList(int page) {
        return kinopoiskClient.getFilms(page);
    }

    @Override
    public FiltersResponseDto getFilters() {
        return kinopoiskClient.getFilters();
    }

    @Override
    public ExternalSourcesResponseDto getExternalSources(Integer kinopoiskId) {
        return kinopoiskClient.getExternalSources(kinopoiskId);
    }

    @Retryable(
            retryFor = {KinopoiskApiException.class, RetryableException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000),
            exceptionExpression = "#{#root instanceof T(feign.RetryableException) || " +
                    "#{#root.apiStatusCode == 429 || #root.apiStatusCode >= 500}"
    )
    @Override
    public MovieDetailsDto getMovieDetails(Integer kinopoiskId) {
        return kinopoiskClient.getMovieDetails(kinopoiskId);
    }
}
