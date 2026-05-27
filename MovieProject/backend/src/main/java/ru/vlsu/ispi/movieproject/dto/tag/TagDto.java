package ru.vlsu.ispi.movieproject.dto.tag;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TagDto {
    private Long id;
    private String name;
    private String type;
}
