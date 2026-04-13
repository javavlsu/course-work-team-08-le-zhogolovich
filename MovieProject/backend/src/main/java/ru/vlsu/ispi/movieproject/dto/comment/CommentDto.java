package ru.vlsu.ispi.movieproject.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private Long userId;
    private Long movieId;
    private String content;
    private LocalDateTime createdAt;
}
