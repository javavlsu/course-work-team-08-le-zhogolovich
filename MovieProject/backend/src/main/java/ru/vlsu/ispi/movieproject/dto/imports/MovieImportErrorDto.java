package ru.vlsu.ispi.movieproject.dto.imports;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MovieImportErrorDto {
    private String movieName;
    private String error;
    private String posterUrl;
}
