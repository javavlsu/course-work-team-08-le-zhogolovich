package ru.vlsu.ispi.movieproject.dto.genre;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GenreDto {
    private Long id;
    private String name;
}
