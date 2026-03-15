package ru.vlsu.ispi.movieproject.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompilationRatingId implements Serializable {
    private Long userId;
    private Long collectionId;
}
