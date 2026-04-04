package ru.vlsu.ispi.movieproject.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.vlsu.ispi.movieproject.dto.compilation.CompilationDto;
import ru.vlsu.ispi.movieproject.dto.compilation.CreateCompilationRequest;
import ru.vlsu.ispi.movieproject.dto.compilation.UpdateCompilationRequest;
import ru.vlsu.ispi.movieproject.security.CustomUserDetails;
import ru.vlsu.ispi.movieproject.service.CompilationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/compilations")
@PreAuthorize("isAuthenticated()")
public class CompilationController {
    private final CompilationService compilationService;

    @PreAuthorize("permitAll()")
    @GetMapping()
    public Page<CompilationDto> getCompilations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal CustomUserDetails user) {
        return compilationService.getAll(PageRequest.of(page, size));
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/{id}")
    public CompilationDto getCompilation(@PathVariable Long id) {
        return compilationService.getById(id);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CompilationDto createCompilation(@ModelAttribute CreateCompilationRequest request) {
        return compilationService.createCompilation(request);
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CompilationDto updateCompilation(@PathVariable Long id, @ModelAttribute UpdateCompilationRequest request) {
        return compilationService.editCompilation(id, request);
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
        return compilationService.getUserCompilations();
    }

    @PostMapping("/{compilationId}/movie/{movieId}")
    public void addMovieToCompilation(@PathVariable Long compilationId, @PathVariable Long movieId) {
        compilationService.addMovie(compilationId, movieId);
    }

    @DeleteMapping("/{compilationId}/movie/{movieId}")
    public void removeMovieFromCompilation(@PathVariable Long compilationId, @PathVariable Long movieId) {
        compilationService.removeMovie(compilationId, movieId);
    }
}
