package ru.vlsu.ispi.movieproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vlsu.ispi.movieproject.model.ReviewLike;
import ru.vlsu.ispi.movieproject.model.ReviewLikeId;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, ReviewLikeId> {
    int countByReview_Id(long reviewId);
    boolean existsById(ReviewLikeId id);
    void deleteById(ReviewLikeId id);
}
