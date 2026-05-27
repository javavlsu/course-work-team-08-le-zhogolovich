package ru.vlsu.ispi.movieproject.dto.review;

import lombok.Data;

@Data
public class SearchReviewRequest {
    private String query;
    private String sortBy;
    private String sortOrder;
}
