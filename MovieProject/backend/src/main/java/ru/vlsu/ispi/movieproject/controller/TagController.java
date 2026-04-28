package ru.vlsu.ispi.movieproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.vlsu.ispi.movieproject.dto.tag.CreateTagRequest;
import ru.vlsu.ispi.movieproject.dto.tag.EditTagRequest;
import ru.vlsu.ispi.movieproject.dto.tag.TagDto;
import ru.vlsu.ispi.movieproject.service.TagService;

@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagController {
    private final TagService tagService;

    @PostMapping
    public TagDto createTag(@RequestBody @Valid CreateTagRequest request) {
        return tagService.create(request);
    }

    @PatchMapping("/{id}")
    public TagDto editTag(@PathVariable Long id, @RequestBody @Valid EditTagRequest request) {
        return tagService.edit(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteTag(@PathVariable Long id) {
        tagService.delete(id);
    }

    @GetMapping("/search")
    public Page<TagDto> searchTags(
            @RequestParam(required = false) String query,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return tagService.search(query, pageable);
    }

}
