package ru.vlsu.ispi.movieproject.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(length = 100)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(name = "avatar_url", length = 1000)
    private String avatarUrl;

    @OneToMany(mappedBy = "author")
    private Set<Review> reviews = new HashSet<>();

    @OneToMany(mappedBy = "author")
    private Set<Compilation> compilations = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<MovieRating> movieRatings = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<CollectionRating> collectionRatings = new HashSet<>();
}