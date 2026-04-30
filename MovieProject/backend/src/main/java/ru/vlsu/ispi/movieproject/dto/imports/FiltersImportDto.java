package ru.vlsu.ispi.movieproject.dto.imports;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FiltersImportDto {
    private ImportResult genreImportResult;
    private ImportResult countryImportResult;
}
