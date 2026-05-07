package ru.vlsu.ispi.movieproject.service.impl;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vlsu.ispi.movieproject.dto.review.CreateReviewRequest;
import ru.vlsu.ispi.movieproject.dto.review.EditReviewRequest;
import ru.vlsu.ispi.movieproject.dto.review.ReviewDto;
import ru.vlsu.ispi.movieproject.dto.review.SearchReviewRequest;
import ru.vlsu.ispi.movieproject.enums.ReviewStatus;
import ru.vlsu.ispi.movieproject.exception.ReviewNotFoundException;
import ru.vlsu.ispi.movieproject.mapper.ReviewMapper;
import ru.vlsu.ispi.movieproject.model.Movie;
import ru.vlsu.ispi.movieproject.model.Review;
import ru.vlsu.ispi.movieproject.model.ReviewLike;
import ru.vlsu.ispi.movieproject.model.ReviewLikeId;
import ru.vlsu.ispi.movieproject.model.User;
import ru.vlsu.ispi.movieproject.projection.ReviewProjection;
import ru.vlsu.ispi.movieproject.repository.ReviewLikeRepository;
import ru.vlsu.ispi.movieproject.repository.ReviewRepository;
import ru.vlsu.ispi.movieproject.service.CurrentUserService;
import ru.vlsu.ispi.movieproject.service.ReviewService;
import ru.vlsu.ispi.movieproject.util.FillTopUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final CurrentUserService currentUserService;
    private final EntityManager entityManager;
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final ReviewLikeRepository reviewLikeRepository;

    @Override
    public Page<ReviewDto> searchReviews(SearchReviewRequest request, Pageable pageable) {
        Long userId = currentUserService.getCurrentUserID();

        return reviewRepository.search(request.getQuery(), request.getSortBy(), request.getSortOrder(), userId, pageable)
                .map(reviewMapper::toDto);
    }

    @Override
    public ReviewDto getReviewById(Long reviewId) {
        Long userId = currentUserService.getCurrentUserID();

        return reviewRepository.findReviewById(reviewId, userId)
                .map(reviewMapper::toDto)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));
    }

    @Override
    public List<ReviewDto> getReviewsByMovieId(Long movieId) {
        Long userId = currentUserService.getCurrentUserID();

        return reviewRepository.findReviewsByMovieId(movieId, userId)
                .stream()
                .map(reviewMapper::toDto)
                .toList();
    }

    @Override
    public List<ReviewDto> getTop10Reviews() {
        Long userId = currentUserService.getCurrentUserID();

        LocalDateTime fromDate = LocalDateTime.now().minusDays(7);

        List<ReviewProjection> top = reviewRepository.findTopReviewsLastWeek(fromDate, userId, PageRequest.of(0,10));

        List<ReviewProjection> fullTop = FillTopUtil.fillTop(top, 10,
                excludeIds -> reviewRepository.findLatest(excludeIds, userId, PageRequest.of(0, 10 - top.size())),
                ReviewProjection::getId);

        return fullTop.stream().map(reviewMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public ReviewDto create(CreateReviewRequest request) {
        Long userId = currentUserService.getCurrentUserID();

        Review review = new Review();
        review.setAuthor(entityManager.getReference(User.class, userId));
        review.setTitle(request.getTitle());
        review.setContent(request.getContent());
        review.setMovie(entityManager.getReference(Movie.class, request.getMovieId()));
        review.setStatus(request.getIsPublish() ? ReviewStatus.PUBLISHED : ReviewStatus.DRAFT);

        Review saved = reviewRepository.save(review);

        return reviewRepository.findReviewById(saved.getId(), userId)
                .map(reviewMapper::toDto)
                .orElseThrow(() -> new ReviewNotFoundException(saved.getId()));
    }

    @Override
    public ReviewDto edit(Long reviewId, EditReviewRequest request) {
        Long userId = currentUserService.getCurrentUserID();

        Review review = reviewRepository.findById(reviewId).
                orElseThrow(() -> new ReviewNotFoundException(reviewId));

        checkOwner(review, userId);

        if (request.getTitle() != null) {
            review.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            review.setContent(request.getContent());
        }
        if (Boolean.TRUE.equals(request.getIsPublish())) {
            review.setStatus(ReviewStatus.PUBLISHED);
        } else review.setStatus(ReviewStatus.DRAFT);

        reviewRepository.save(review);

        return reviewRepository.findReviewById(reviewId, userId)
                .map(reviewMapper::toDto)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));
    }

    @Override
    public void delete(Long reviewId) {
        Long userId = currentUserService.getCurrentUserID();

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));
        checkOwner(review, userId);

        reviewRepository.delete(review);
    }

    @Override
    public void like(Long reviewId) {
        Long userId = currentUserService.getCurrentUserID();

        ReviewLikeId reviewLikeId = new ReviewLikeId(userId, reviewId);

        if (reviewLikeRepository.existsById(reviewLikeId)) return;

        ReviewLike like = new ReviewLike();
        like.setId(reviewLikeId);
        like.setReview(entityManager.getReference(Review.class, reviewId));
        like.setUser(entityManager.getReference(User.class, userId));

        reviewLikeRepository.save(like);
        reviewRepository.incrementLikes(reviewId);
    }

    @Override
    public void unlike(Long reviewId) {
        Long userId = currentUserService.getCurrentUserID();

        reviewLikeRepository.deleteById(new ReviewLikeId(userId, reviewId));
        reviewRepository.decrementLikes(reviewId);
    }

    @Override
    public List<ReviewDto> getCurrentUserReviews() {
        Long currentUserId = currentUserService.getCurrentUserID();

        return reviewRepository.findUserReviews(currentUserId, currentUserId).stream()
                .map(reviewMapper::toDto)
                .toList();
    }

    @Override
    public List<ReviewDto> getUserReviews(Long userId) {
        Long currentUserId = currentUserService.getCurrentUserID();

        return reviewRepository.findUserReviews(userId, currentUserId).stream()
                .map(reviewMapper::toDto)
                .toList();
    }

    @Override
    public List<ReviewDto> getCurrentUserLikedReviews() {
        Long userId = currentUserService.getCurrentUserID();

        return reviewRepository.findAllLikedByUserId(userId, userId)
                .stream()
                .map(reviewMapper::toDto)
                .toList();
    }

    @Override
    public List<ReviewDto> getUserLikedReviews(Long userId) {
        Long currentUserId = currentUserService.getCurrentUserID();

        return reviewRepository.findAllLikedByUserId(userId, currentUserId)
                .stream()
                .map(reviewMapper::toDto)
                .toList();
    }

    private void checkOwner(Review review, Long userId) {
        if (!review.getAuthor().getId().equals(userId)) {
            throw new AccessDeniedException("Вы не являетесь владельцем этой рецензии");
        }
    }
}
