package ru.vlsu.ispi.movieproject.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "review_like")
@Getter @Setter
@NoArgsConstructor
public class ReviewLike extends AuditableEntity{
    @EmbeddedId
    private ReviewLikeId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("reviewId")
    @JoinColumn(name = "review_id")
    private Review review;
}
