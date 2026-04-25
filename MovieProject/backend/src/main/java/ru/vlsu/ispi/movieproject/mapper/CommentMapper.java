package ru.vlsu.ispi.movieproject.mapper;

import org.springframework.stereotype.Component;
import ru.vlsu.ispi.movieproject.dto.comment.CommentDto;
import ru.vlsu.ispi.movieproject.model.Comment;

@Component
public class CommentMapper {
    public CommentDto toDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getUser().getUsername(),
                comment.getUser().getAvatarUrl(),
                comment.getMovie().getId(),
                comment.getContent(),
                comment.getCreatedAt()
        );
    }
}
