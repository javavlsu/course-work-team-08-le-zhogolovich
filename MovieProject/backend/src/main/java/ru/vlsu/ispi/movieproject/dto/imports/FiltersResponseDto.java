package ru.vlsu.ispi.movieproject.dto.imports;

import lombok.Data;
import ru.vlsu.ispi.movieproject.dto.movie.CountryDto;
import ru.vlsu.ispi.movieproject.dto.movie.GenreDto;

import java.util.List;

@Data
public class FiltersResponseDto {
    private List<GenreDto> genres;
    private List<CountryDto> countries;
}
