package ru.vlsu.ispi.movieproject.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.vlsu.ispi.movieproject.config.FeignConfig;
import ru.vlsu.ispi.movieproject.dto.imports.ExternalSourcesResponseDto;
import ru.vlsu.ispi.movieproject.dto.imports.FiltersResponseDto;
import ru.vlsu.ispi.movieproject.dto.imports.MovieListResponseDto;
import ru.vlsu.ispi.movieproject.dto.movie.MovieDetailsDto;

@FeignClient(
        name = "kinopoisk-client",
        url = "${kinopoisk.api.url}",
        configuration = FeignConfig.class
)
public interface KinopoiskClient {
    @GetMapping("/api/v2.2/films")
    MovieListResponseDto getFilms(@RequestParam("page") int pageNum);

    @GetMapping("/api/v2.2/films/filters")
    FiltersResponseDto getFilters();

    @GetMapping("/api/v2.2/films/{id}/external_sources")
    ExternalSourcesResponseDto getExternalSources(@PathVariable("id") Integer id);

    @GetMapping("/api/v2.2/films/{id}")
    MovieDetailsDto getMovieDetails(@PathVariable("id") Integer kinopoiskId);
}
