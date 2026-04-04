package ru.vlsu.ispi.movieproject.dto.movie;

import lombok.Data;

import java.util.List;

@Data
public class AddMovieToCompilationsRequest {
    private List<Long> compilationIds;
}
