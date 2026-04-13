package ru.vlsu.ispi.movieproject.dto.movie;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RateMovieRequest {
    @NotNull
    @DecimalMin("1.0")
    @DecimalMax("10.0")
    private Double rating;
}
