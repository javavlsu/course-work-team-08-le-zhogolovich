package ru.vlsu.ispi.movieproject.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "countrie")
@Getter @Setter
@NoArgsConstructor
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "kinopoisk_id")
    private Integer kinopoiskId;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "countries")
    private Set<Movie> movies = new HashSet<>();
}
