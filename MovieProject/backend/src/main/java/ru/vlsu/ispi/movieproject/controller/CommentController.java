package ru.vlsu.ispi.movieproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.vlsu.ispi.movieproject.dto.comment.CommentDto;
import ru.vlsu.ispi.movieproject.dto.comment.CommentRequest;
import ru.vlsu.ispi.movieproject.service.CommentService;


@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/movie/{movieId}")
    public CommentDto addComment(@PathVariable Long movieId, @RequestBody @Valid CommentRequest request) {
        return commentService.createComment(movieId, request);
    }

    @PatchMapping("/{commentId}")
    public CommentDto editComment(@PathVariable Long commentId, @RequestBody @Valid CommentRequest request) {
        return commentService.editComment(commentId, request);
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/movie/{movieId}")
    public Page<CommentDto> getComments(@PathVariable Long movieId,
                                        @PageableDefault(size = 20, sort = {"createdAt", "id"},
                                                direction = Sort.Direction.DESC) Pageable pageable) {
        return commentService.getComments(movieId, pageable);
    }
}
