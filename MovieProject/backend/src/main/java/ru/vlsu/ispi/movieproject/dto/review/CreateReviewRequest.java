package ru.vlsu.ispi.movieproject.dto.review;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateReviewRequest {
    @NotNull
    private Long movieId;

    @NotBlank
    @Size(min = 10, max = 500)
    private String title;

    @NotBlank
    @Size(max = 5000)
    private String content;

    @NotNull
    private Boolean isPublish;
}
