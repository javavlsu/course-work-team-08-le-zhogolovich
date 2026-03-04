package ru.vlsu.ispi.movieproject.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tag")
@Getter @Setter
@NoArgsConstructor
public class Tag extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TagType type;

    @ManyToMany(mappedBy = "tags")
    private Set<Movie> movies = new HashSet<>();
}