package ru.vlsu.ispi.movieproject.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.vlsu.ispi.movieproject.dto.tag.CreateTagRequest;
import ru.vlsu.ispi.movieproject.dto.tag.EditTagRequest;
import ru.vlsu.ispi.movieproject.dto.tag.TagDto;

public interface TagService {
    TagDto create(CreateTagRequest request);
    TagDto edit(Long id, EditTagRequest request);
    void delete(Long id);
    Page<TagDto> search(String name, Pageable pageable);
}
