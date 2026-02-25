package ru.vlsu.ispi.movieproject.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "collection_ratings")
@Getter @Setter
@NoArgsConstructor
public class CollectionRating {
    @EmbeddedId
    private CollectionRatingId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("collectionId")
    @JoinColumn(name = "collection_id")
    private Collection movie;

    private Integer rating;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
