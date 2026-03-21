package ru.vlsu.ispi.movieproject.dto.imports;

import lombok.Data;

import java.util.List;

@Data
public class ExternalSourcesResponseDto {
    private Integer total;
    private List<ExternalSourceDto> items;
}
