package ru.vlsu.ispi.movieproject.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.MapsId;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "compilation_like")
@Getter
@Setter
@NoArgsConstructor
public class CompilationLike extends AuditableEntity{
    @EmbeddedId
    private CompilationLikeId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("compilationId")
    @JoinColumn(name = "compilation_id")
    private Compilation compilation;
}
