package ru.vlsu.ispi.movieproject.mapper;

import org.springframework.stereotype.Component;
import ru.vlsu.ispi.movieproject.dto.review.ReviewDto;
import ru.vlsu.ispi.movieproject.model.Review;

@Component
public class ReviewMapper {
    public ReviewDto toDto(Review review, int likesCount) {
        return new ReviewDto(
                review.getId(),
                review.getMovie().getId(),
                review.getAuthor().getId(),
                review.getTitle(),
                review.getContent(),
                review.getStatus().toString(),
                review.getCreatedAt(),
                likesCount
        );
    }
}
