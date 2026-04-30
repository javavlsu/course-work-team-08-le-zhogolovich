package ru.vlsu.ispi.movieproject.dto.imports;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MovieImportResultDto {
    private ImportResult results;
    private List<MovieImportErrorDto> errors = new ArrayList<>();
}
