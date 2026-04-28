package ru.vlsu.ispi.movieproject.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.vlsu.ispi.movieproject.dto.comment.CommentDto;
import ru.vlsu.ispi.movieproject.dto.comment.CommentRequest;

public interface CommentService {
    CommentDto createComment(Long movieId, CommentRequest request);
    void deleteComment(Long commentId);
    CommentDto editComment(Long commentId, CommentRequest request);
    Page<CommentDto> getComments(Long movieId, Pageable pageable);
}
