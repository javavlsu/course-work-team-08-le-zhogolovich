package ru.vlsu.ispi.movieproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.vlsu.ispi.movieproject.dto.compilation.CompilationDto;
import ru.vlsu.ispi.movieproject.dto.compilation.CreateCompilationRequest;
import ru.vlsu.ispi.movieproject.dto.compilation.UpdateCompilationRequest;
import ru.vlsu.ispi.movieproject.service.CompilationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/compilations")
public class CompilationController {
    private final CompilationService compilationService;

    @GetMapping()
    public Page<CompilationDto> getCompilations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return compilationService.getAll(PageRequest.of(page, size));
    }

    @GetMapping("/{id}")
    public CompilationDto getCompilation(@PathVariable Long id) {
        return compilationService.getById(id);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CompilationDto createCompilation(@ModelAttribute @Valid CreateCompilationRequest request) {
        return compilationService.createCompilation(request);
    }

    @PatchMapping(value = "/{id}")
    public CompilationDto updateCompilation(@PathVariable Long id, @RequestBody @Valid UpdateCompilationRequest request) {
        return compilationService.editCompilation(id, request);
    }

    @PatchMapping(value = "/{id}/cover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CompilationDto updateCompilationCover(@PathVariable Long id, @RequestParam("file") MultipartFile cover) {
        return compilationService.updateCover(id, cover);
    }

    @DeleteMapping("/{id}")
    public void deleteCompilation(@PathVariable Long id) {
        compilationService.deleteCompilation(id);
    }

    @PostMapping("/{id}/like")
    public void likeCompilation(@PathVariable Long id) {
        compilationService.like(id);
    }

    @DeleteMapping("/{id}/like")
    public void unlikeCompilation(@PathVariable Long id) {
        compilationService.unlike(id);
    }

    @GetMapping("/my")
    public List<CompilationDto> getMyCompilations() {
        return compilationService.getCurrentUserCompilations();
    }

    @GetMapping("/user/{userId}")
    public List<CompilationDto> getUserCompilations(@PathVariable Long userId) {
        return compilationService.getUserCompilations(userId);
    }

    @DeleteMapping("/{compilationId}/movie/{movieId}")
    public CompilationDto removeMovieFromCompilation(@PathVariable Long compilationId, @PathVariable Long movieId) {
        return compilationService.removeMovie(compilationId, movieId);
    }

    @PostMapping("/{id}/subscribe")
    public void subscribeCompilation(@PathVariable Long id) {
        compilationService.subscribe(id);
    }

    @DeleteMapping("/{id}/subscribe")
    public void unsubscribeCompilation(@PathVariable Long id) {
        compilationService.unsubscribe(id);
    }
}
