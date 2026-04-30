package ru.vlsu.ispi.movieproject.dto.imports;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EnrichmentErrorDto {
    private Long movieId;
    private String error;
}
