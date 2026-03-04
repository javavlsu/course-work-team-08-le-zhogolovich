package ru.vlsu.ispi.movieproject.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "compilation_rating")
@Getter @Setter
@NoArgsConstructor
public class CollectionRating extends AuditableEntity{
    @EmbeddedId
    private CompilationRatingId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("collectionId")
    @JoinColumn(name = "collection_id")
    private Compilation movie;

    private Integer rating;
}
