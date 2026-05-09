package ru.vlsu.ispi.movieproject.service;

import ru.vlsu.ispi.movieproject.dto.imports.ImportResult;
import ru.vlsu.ispi.movieproject.dto.imports.ImportGenreDto;

import java.util.List;

public interface GenreImportService {
    ImportResult importGenres(List<ImportGenreDto> genres);
}
