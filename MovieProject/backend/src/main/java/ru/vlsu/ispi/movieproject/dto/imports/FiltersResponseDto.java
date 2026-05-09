package ru.vlsu.ispi.movieproject.dto.imports;

import lombok.Data;

import java.util.List;

@Data
public class FiltersResponseDto {
    private List<ImportGenreDto> genres;
    private List<ImportCountryDto> countries;
}
