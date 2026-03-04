package ru.vlsu.ispi.movieproject.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompilationRatingId implements Serializable {
    private Long userId;
    private Long collectionId;
}
