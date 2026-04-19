package ru.vlsu.ispi.movieproject.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.vlsu.ispi.movieproject.dto.review.CreateReviewRequest;
import ru.vlsu.ispi.movieproject.dto.review.EditReviewRequest;
import ru.vlsu.ispi.movieproject.dto.review.ReviewDto;

public interface ReviewService {
    ReviewDto create(CreateReviewRequest request);
    ReviewDto edit(Long reviewId, EditReviewRequest request);
    void delete(Long reviewId);
    void like(Long reviewId);
    void unlike(Long reviewId);
    ReviewDto getReview(Long reviewId);
    Page<ReviewDto> getReviews(Pageable pageable);
}
