package ru.vlsu.ispi.movieproject.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "review")
@Getter @Setter
@NoArgsConstructor
public class Review extends AuditableEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @Column(length = 500)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private Integer rating;

    @Enumerated(EnumType.STRING)
    private ReviewStatus status;
}
