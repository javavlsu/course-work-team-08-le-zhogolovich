package ru.vlsu.ispi.movieproject.dto.review;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ReviewDto {
    private Long id;
    private Long movieId;
    private String authorName;
    private String authorAvatar;
    private String title;
    private String content;
    private String status;
    private LocalDateTime createdAt;
    private int likesCount;
}
