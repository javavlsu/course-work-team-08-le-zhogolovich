package ru.vlsu.ispi.movieproject.service;

import ru.vlsu.ispi.movieproject.dto.imports.ImportResult;
import ru.vlsu.ispi.movieproject.dto.movie.CountryDto;

import java.util.List;

public interface CountryImportService {
    ImportResult importCountries(List<CountryDto> countries);
}
