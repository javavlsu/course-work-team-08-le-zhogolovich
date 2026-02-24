package ru.vlsu.ispi.movieproject.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_follow",
        uniqueConstraints = @UniqueConstraint(columnNames = {"follower_id", "followed_user_id"}))
@Getter @Setter
public class UserFollow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id")
    private User follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followed_user_id")
    private User followedUser;

    @Column(name = "followed_at", nullable = false)
    private LocalDateTime followedAt;
}