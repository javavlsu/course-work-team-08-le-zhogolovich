package ru.vlsu.ispi.movieproject.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.vlsu.ispi.movieproject.dto.review.CreateReviewRequest;
import ru.vlsu.ispi.movieproject.dto.review.EditReviewRequest;
import ru.vlsu.ispi.movieproject.dto.review.ReviewDto;
import ru.vlsu.ispi.movieproject.dto.review.SearchReviewRequest;
import ru.vlsu.ispi.movieproject.service.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping
    public Page<ReviewDto> getReviews(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder,

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        SearchReviewRequest request = new SearchReviewRequest();
        request.setQuery(query);
        request.setSortBy(sortBy);
        request.setSortOrder(sortOrder);

        return reviewService.searchReviews(request, PageRequest.of(page, size));
    }

    @GetMapping("/{id}")
    public ReviewDto getReview(@PathVariable Long id) {
        return reviewService.getReviewById(id);
    }

    @PostMapping
    public ReviewDto createReview(@RequestBody CreateReviewRequest request) {
        return reviewService.create(request);
    }

    @PatchMapping("/{id}")
    public ReviewDto editReview(@PathVariable Long id, @RequestBody EditReviewRequest request) {
        return reviewService.edit(id, request);
    }

    @GetMapping("/my")
    public List<ReviewDto> getMyReviews() {
        return reviewService.getCurrentUserReviews();
    }

    @GetMapping("/user/{userId}")
    public List<ReviewDto> getUserReviews(@PathVariable Long userId) {
        return reviewService.getUserReviews(userId);
    }

    @GetMapping("/my/liked")
    public List<ReviewDto> getMyLikedReviews() {
        return reviewService.getCurrentUserLikedReviews();
    }

    @GetMapping("/user/{userId}/liked")
    public List<ReviewDto> getUserLikedReviews(@PathVariable Long userId) {
        return reviewService.getUserLikedReviews(userId);
    }

    @GetMapping("/movie/{movieId}")
    public List<ReviewDto> getMovieReviews(@PathVariable Long movieId) {
        return reviewService.getReviewsByMovieId(movieId);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable Long id) {
        reviewService.delete(id);
    }

    @PostMapping("/{id}/like")
    public void likeReview(@PathVariable Long id) {
        reviewService.like(id);
    }

    @DeleteMapping("/{id}/like")
    public void unlikeReview(@PathVariable Long id) {
        reviewService.unlike(id);
    }

    @GetMapping("/top/top10")
    public List<ReviewDto> getTop10Reviews() {
        return reviewService.getTop10Reviews();
    }
}
