package ru.vlsu.ispi.movieproject.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.vlsu.ispi.movieproject.dto.review.CreateReviewRequest;
import ru.vlsu.ispi.movieproject.dto.review.EditReviewRequest;
import ru.vlsu.ispi.movieproject.dto.review.ReviewDto;
import ru.vlsu.ispi.movieproject.dto.review.SearchReviewRequest;

import java.util.List;

public interface ReviewService {
    Page<ReviewDto> searchReviews(SearchReviewRequest request, Pageable pageable);
    ReviewDto getReviewById(Long reviewId);
    List<ReviewDto> getReviewsByMovieId(Long movieId);
    List<ReviewDto> getTop10Reviews();
    ReviewDto create(CreateReviewRequest request);
    ReviewDto edit(Long reviewId, EditReviewRequest request);
    void delete(Long reviewId);
    void like(Long reviewId);
    void unlike(Long reviewId);
    List<ReviewDto> getCurrentUserReviews();
    List<ReviewDto> getUserReviews(Long userId);
    List<ReviewDto> getCurrentUserLikedReviews();
    List<ReviewDto> getUserLikedReviews(Long userId);
}
