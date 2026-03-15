package ru.vlsu.ispi.movieproject.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import ru.vlsu.ispi.movieproject.config.FeignConfig;
import ru.vlsu.ispi.movieproject.dto.imports.FiltersResponseDto;
import ru.vlsu.ispi.movieproject.dto.movie.MovieListResponseDto;

@FeignClient(
        name = "kinopoisk-client",
        url = "${kinopoisk.api.url}",
        configuration = FeignConfig.class
)
public interface KinopoiskClient {
    @GetMapping("/api/v2.2/films")
    MovieListResponseDto getFilms();

    @GetMapping("/api/v2.2/films/filters")
    FiltersResponseDto getFilters();
}
