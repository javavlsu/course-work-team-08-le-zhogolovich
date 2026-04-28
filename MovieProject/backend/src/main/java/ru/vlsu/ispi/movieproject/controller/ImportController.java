package ru.vlsu.ispi.movieproject.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.vlsu.ispi.movieproject.dto.imports.ImportResult;
import ru.vlsu.ispi.movieproject.service.GenreCountryImportService;
import ru.vlsu.ispi.movieproject.service.MovieImportService;

@RestController
@RequestMapping("/api/import")
@RequiredArgsConstructor
public class ImportController {
    private final MovieImportService movieImportService;
    private final GenreCountryImportService genreCountryImportService;

    @PostMapping("/movies")
    public ImportResult importMovies() {
        return movieImportService.importMovies();
    }

    @PostMapping("/filters")
    public void importFilters() {
        genreCountryImportService.importGenreCountry();
    }
}
