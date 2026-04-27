package ru.vlsu.ispi.movieproject.service.impl;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import ru.vlsu.ispi.movieproject.dto.comment.CommentDto;
import ru.vlsu.ispi.movieproject.dto.comment.CommentRequest;
import ru.vlsu.ispi.movieproject.exception.CommentNotFoundException;
import ru.vlsu.ispi.movieproject.mapper.CommentMapper;
import ru.vlsu.ispi.movieproject.model.Comment;
import ru.vlsu.ispi.movieproject.model.Movie;
import ru.vlsu.ispi.movieproject.model.User;
import ru.vlsu.ispi.movieproject.repository.CommentRepository;
import ru.vlsu.ispi.movieproject.service.CommentService;
import ru.vlsu.ispi.movieproject.service.CurrentUserService;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CurrentUserService currentUserService;
    private final CommentRepository commentRepository;
    private final EntityManager entityManager;
    private final CommentMapper commentMapper;

    @Override
    public CommentDto createComment(Long movieId, CommentRequest request) {
        Long userId = currentUserService.getCurrentUserID();

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setUser(entityManager.find(User.class, userId));
        comment.setMovie(entityManager.find(Movie.class, movieId));

        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Override
    public void deleteComment(Long commentId) {
        Long userId = currentUserService.getCurrentUserID();

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));
        if (!comment.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Нельзя удалить чужой комментарий");
        }
        commentRepository.delete(comment);
    }

    @Override
    public CommentDto editComment(Long commentId, CommentRequest request) {
        Long userId = currentUserService.getCurrentUserID();

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));
        if (!comment.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Нельзя удалить чужой комментарий");
        }

        comment.setContent(request.getContent());

        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Override
    public Page<CommentDto> getComments(Long movieId, Pageable pageable) {
        return commentRepository.findByMovieId(movieId, pageable).map(commentMapper::toDto);
    }
}
