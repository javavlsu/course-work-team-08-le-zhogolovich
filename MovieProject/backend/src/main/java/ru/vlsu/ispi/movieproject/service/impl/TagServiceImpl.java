package ru.vlsu.ispi.movieproject.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vlsu.ispi.movieproject.dto.tag.CreateTagRequest;
import ru.vlsu.ispi.movieproject.dto.tag.EditTagRequest;
import ru.vlsu.ispi.movieproject.dto.tag.TagDto;
import ru.vlsu.ispi.movieproject.enums.TagType;
import ru.vlsu.ispi.movieproject.exception.TagAlreadyExistsException;
import ru.vlsu.ispi.movieproject.exception.TagNotFoundException;
import ru.vlsu.ispi.movieproject.mapper.TagMapper;
import ru.vlsu.ispi.movieproject.model.Tag;
import ru.vlsu.ispi.movieproject.repository.TagRepository;
import ru.vlsu.ispi.movieproject.service.TagService;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    @Override
    public TagDto create(CreateTagRequest request) {
        if (tagRepository.existsByName(request.getName())) {
            throw new TagAlreadyExistsException();
        }
        Tag tag = new Tag();
        tag.setName(request.getName());
        tag.setType(TagType.fromString(request.getType()));

        return tagMapper.toDto(tagRepository.save(tag));
    }

    @Override
    @Transactional
    public TagDto edit(Long id, EditTagRequest request) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new TagNotFoundException(id));

        if (request.getName() != null) {
            tag.setName(request.getName());
        }
        if (request.getType() != null) {
            tag.setType(TagType.fromString(request.getType()));
        }
        return tagMapper.toDto(tag);
    }

    @Override
    public void delete(Long id) {
        if (!tagRepository.existsById(id)) {
            throw new TagNotFoundException(id);
        }

        tagRepository.deleteById(id);
    }

    @Override
    public Page<TagDto> search(String query, Pageable pageable) {
        if (query == null || query.length() < 2) {
            return tagRepository.findAllByOrderByNameAsc(pageable).map(tagMapper::toDto);
        }

        return tagRepository.findByNameContainingIgnoreCase(query, pageable).map(tagMapper::toDto);
    }
}
