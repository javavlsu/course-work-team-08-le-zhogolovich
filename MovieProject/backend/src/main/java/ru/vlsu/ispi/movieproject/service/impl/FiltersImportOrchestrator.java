package ru.vlsu.ispi.movieproject.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vlsu.ispi.movieproject.dto.imports.FiltersImportDto;
import ru.vlsu.ispi.movieproject.dto.imports.FiltersResponseDto;
import ru.vlsu.ispi.movieproject.dto.imports.ImportResult;
import ru.vlsu.ispi.movieproject.service.CountryImportService;
import ru.vlsu.ispi.movieproject.service.GenreImportService;
import ru.vlsu.ispi.movieproject.service.ImportService;
import ru.vlsu.ispi.movieproject.service.KinopoiskApiService;

@Service
@RequiredArgsConstructor
public class FiltersImportOrchestrator implements ImportService<FiltersImportDto> {
    private final KinopoiskApiService kinopoiskApiService;
    private final GenreImportService genreImportService;
    private final CountryImportService countryImportService;

    @Transactional
    @Override
    public FiltersImportDto importData() {
        FiltersResponseDto filters = kinopoiskApiService.getFilters();

        ImportResult genreImportRes = genreImportService.importGenres(filters.getGenres());
        ImportResult countryImportRes = countryImportService.importCountries(filters.getCountries());

        return new FiltersImportDto(genreImportRes, countryImportRes);
    }
}
