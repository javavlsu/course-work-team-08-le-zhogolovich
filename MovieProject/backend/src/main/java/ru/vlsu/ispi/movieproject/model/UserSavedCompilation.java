package ru.vlsu.ispi.movieproject.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_saved_compilation")
@Getter @Setter
@NoArgsConstructor
public class UserSavedCompilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "collection_id", nullable = false)
    private Compilation compilation;

    @Column(name = "saved_at")
    private LocalDateTime savedAt;
}
