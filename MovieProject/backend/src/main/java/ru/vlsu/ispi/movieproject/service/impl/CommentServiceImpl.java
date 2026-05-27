package ru.vlsu.ispi.movieproject.service.impl;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import ru.vlsu.ispi.movieproject.util.AccessUtil;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CurrentUserService currentUserService;
    private final CommentRepository commentRepository;
    private final EntityManager entityManager;
    private final CommentMapper commentMapper;
    private final AccessUtil accessUtil;

    @Override
    public CommentDto createComment(Long movieId, CommentRequest request) {
        Long userId = currentUserService.getCurrentUserID();

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setUser(entityManager.getReference(User.class, userId));
        comment.setMovie(entityManager.getReference(Movie.class, movieId));

        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Override
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));
        accessUtil.checkOwnerOrAdmin(comment.getUser().getId());

        commentRepository.delete(comment);
    }

    @Override
    public CommentDto editComment(Long commentId, CommentRequest request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));
        accessUtil.checkOwnerOrAdmin(comment.getUser().getId());

        comment.setContent(request.getContent());

        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Override
    public Page<CommentDto> getComments(Long movieId, Pageable pageable) {
        return commentRepository.findByMovieId(movieId, pageable).map(commentMapper::toDto);
    }
}
