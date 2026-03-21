package ru.vlsu.ispi.movieproject.service.impl;

import lombok.RequiredArgsConstructor;
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
        try{
            return kinopoiskClient.getFilms(page);
        }catch (Exception e){
            throw new KinopoiskApiException("Ошибка при обращении к Kinopoisk API - получение фильмов. Сообщение: " + e.getMessage());
        }
    }

    @Override
    public MovieDetailsDto getMovieDetails(Integer kinopoiskId) {
        try{
            return kinopoiskClient.getMovieDetails(kinopoiskId);
        }catch (Exception e){
            throw new KinopoiskApiException("Ошибка при обращении к Kinopoisk API - получение деталей фильма. Сообщение: " + e.getMessage());
        }
    }

    @Override
    public FiltersResponseDto getFilters() {
        try {
            return kinopoiskClient.getFilters();
        }catch (Exception e){
            throw new KinopoiskApiException("Ошибка при обращении к Kinopoisk API - получение жанров и стран. Сообщение: " + e.getMessage());
        }
    }

    @Override
    public ExternalSourcesResponseDto getExternalSources(Integer kinopoiskId) {
        try {
            return kinopoiskClient.getExternalSources(kinopoiskId);
        }catch (Exception e){
            throw new KinopoiskApiException("Ошибка при обращении к Kinopoisk API - получение источников к фильму. Сообщение: " + e.getMessage());
        }
    }


}
