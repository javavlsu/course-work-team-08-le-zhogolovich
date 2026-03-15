package ru.vlsu.ispi.movieproject.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vlsu.ispi.movieproject.client.KinopoiskClient;
import ru.vlsu.ispi.movieproject.dto.imports.FiltersResponseDto;
import ru.vlsu.ispi.movieproject.dto.movie.MovieDetailsDto;
import ru.vlsu.ispi.movieproject.dto.movie.MovieListResponseDto;
import ru.vlsu.ispi.movieproject.exception.KinopoiskApiException;
import ru.vlsu.ispi.movieproject.service.KinopoiskApiService;

@Service
@RequiredArgsConstructor
public class KinopoiskApiServiceImpl implements KinopoiskApiService {
    private final KinopoiskClient kinopoiskClient;

    @Override
    public MovieListResponseDto getMovieList() {
        try{
            return kinopoiskClient.getFilms();
        }catch (Exception e){
            throw new KinopoiskApiException("Ошибка при обращении к Kinoposik API - получение фильмов");
        }
    }

    @Override
    public MovieDetailsDto getMovieDetails() {
        return null;
    }

    @Override
    public FiltersResponseDto getFilters() {
        try {
            return kinopoiskClient.getFilters();
        }catch (Exception e){
            throw new KinopoiskApiException("Ошибка при обращении к Kinoposik API - получение жанров и стран");
        }
    }


}
