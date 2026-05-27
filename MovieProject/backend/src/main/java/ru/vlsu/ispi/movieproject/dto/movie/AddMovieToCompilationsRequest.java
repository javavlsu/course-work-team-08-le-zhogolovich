package ru.vlsu.ispi.movieproject.dto.movie;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AddMovieToCompilationsRequest {
    @NotEmpty
    private List<@NotNull Long> compilationIds;
}
