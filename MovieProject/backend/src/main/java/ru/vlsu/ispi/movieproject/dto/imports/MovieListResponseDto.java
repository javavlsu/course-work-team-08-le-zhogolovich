package ru.vlsu.ispi.movieproject.dto.imports;

import lombok.Data;
import ru.vlsu.ispi.movieproject.dto.movie.MovieImportDto;

import java.util.List;

@Data
public class MovieListResponseDto {
    private Integer total;
    private Integer totalPages;
    private List<MovieImportDto> items;
}
