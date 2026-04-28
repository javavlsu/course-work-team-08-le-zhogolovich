package ru.vlsu.ispi.movieproject.mapper;

import org.springframework.stereotype.Component;
import ru.vlsu.ispi.movieproject.dto.review.ReviewDto;
import ru.vlsu.ispi.movieproject.projection.ReviewProjection;

@Component
public class ReviewMapper {
    public ReviewDto toDto(ReviewProjection p) {
        return new ReviewDto(
                p.getId(),
                p.getMovieId(),
                p.getAuthorName(),
                p.getAuthorAvatar(),
                p.getTitle(),
                p.getContent(),
                p.getStatus(),
                p.getCreatedAt(),
                p.getLikesCount(),
                p.getIsLikedByCurrentUser(),
                p.getMovieCover()
        );
    }
}
