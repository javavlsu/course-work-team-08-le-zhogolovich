package ru.vlsu.ispi.movieproject.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.vlsu.ispi.movieproject.dto.imports.EnrichmentResultDto;
import ru.vlsu.ispi.movieproject.dto.imports.FiltersImportDto;
import ru.vlsu.ispi.movieproject.dto.imports.MovieImportResultDto;
import ru.vlsu.ispi.movieproject.service.EnrichService;
import ru.vlsu.ispi.movieproject.service.ImportService;
import ru.vlsu.ispi.movieproject.service.impl.FiltersImportOrchestrator;

@RestController
@RequestMapping("/api/import")
@RequiredArgsConstructor
public class ImportController {
    private final ImportService<MovieImportResultDto> movieImportService;
    private final FiltersImportOrchestrator filtersImportOrchestrator;
    private final EnrichService enrichService;

    @PostMapping("/movies")
    public MovieImportResultDto importMovies() {
        return movieImportService.importData();
    }

    @PostMapping("/movies/enrich")
    public EnrichmentResultDto importMovieEnrich() throws InterruptedException {
        return enrichService.enrichAll();
    }

    @PostMapping("/filters")
    public FiltersImportDto importFilters() {
        return filtersImportOrchestrator.importData();
    }
}
