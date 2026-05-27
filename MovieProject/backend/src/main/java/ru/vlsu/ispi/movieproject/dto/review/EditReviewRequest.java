package ru.vlsu.ispi.movieproject.dto.review;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EditReviewRequest {
    @Size(min = 10, max = 500)
    private String title;

    @Size(max = 5000)
    private String content;

    private Boolean isPublish;
}
