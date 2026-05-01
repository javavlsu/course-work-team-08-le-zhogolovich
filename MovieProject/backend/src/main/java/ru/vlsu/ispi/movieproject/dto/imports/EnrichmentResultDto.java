package ru.vlsu.ispi.movieproject.dto.imports;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class EnrichmentResultDto {
    private int enriched;
    private int skipped;
    private int failed;
    private List<EnrichmentErrorDto> errors = new ArrayList<>();
}
