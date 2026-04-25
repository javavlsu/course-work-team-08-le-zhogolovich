package ru.vlsu.ispi.movieproject.mapper;

import org.springframework.stereotype.Component;
import ru.vlsu.ispi.movieproject.dto.tag.TagDto;
import ru.vlsu.ispi.movieproject.model.Tag;

@Component
public class TagMapper {
    public TagDto toDto(Tag tag) {
        return new TagDto(
                tag.getId(),
                tag.getName(),
                tag.getType().toString()
        );
    }
}
