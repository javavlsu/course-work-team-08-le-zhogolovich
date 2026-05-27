package ru.vlsu.ispi.movieproject.dto.movie;

import lombok.Data;

@Data
public class SearchMoviesRequest {
    private String query;
    private Long genreId;
    private Long tagId;
    private Long countryId;
    private Integer year;
    private String sortBy;
    private String sortOrder;
}
