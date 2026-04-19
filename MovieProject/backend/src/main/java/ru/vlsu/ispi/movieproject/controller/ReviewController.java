package ru.vlsu.ispi.movieproject.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
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
import ru.vlsu.ispi.movieproject.service.ReviewService;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class ReviewController {
    private final ReviewService reviewService;

    @PreAuthorize("permitAll()")
    @GetMapping
    public Page<ReviewDto> getReviews(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "20") int size) {
        return reviewService.getReviews(PageRequest.of(page, size));
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/{id}")
    public ReviewDto getReview(@PathVariable long id) {
        return reviewService.getReview(id);
    }

    @PostMapping
    public ReviewDto createReview(@RequestBody CreateReviewRequest request) {
        return reviewService.create(request);
    }

    @PatchMapping("/{id}")
    public ReviewDto editReview(@PathVariable Long id, @RequestBody EditReviewRequest request) {
        return reviewService.edit(id, request);
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
}
