package ru.vlsu.ispi.movieproject.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.OneToMany;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(length = 100, nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "about_me", columnDefinition = "TEXT")
    private String aboutMe;

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
    private Set<CompilationRating> compilationRatings = new HashSet<>();
}