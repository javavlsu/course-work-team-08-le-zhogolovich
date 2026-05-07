package ru.vlsu.ispi.movieproject.dto.compilation;

import lombok.Data;

@Data
public class SearchCompilationRequest {
    private String query;
    private String sortBy;
    private String sortOrder;
}
