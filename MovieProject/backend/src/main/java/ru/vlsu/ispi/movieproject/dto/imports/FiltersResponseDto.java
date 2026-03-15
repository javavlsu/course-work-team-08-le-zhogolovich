package ru.vlsu.ispi.movieproject.dto.imports;

import lombok.Data;
import ru.vlsu.ispi.movieproject.dto.movie.CountryDto;
import ru.vlsu.ispi.movieproject.dto.movie.GenreDto;

import java.util.Set;

@Data
public class FiltersResponseDto {
    private Set<GenreDto> genres;
    private Set<CountryDto> countries;
}
