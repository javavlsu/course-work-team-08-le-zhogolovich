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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.vlsu.ispi.movieproject.dto.compilation.CompilationDto;
import ru.vlsu.ispi.movieproject.dto.compilation.CreateCompilationRequest;
import ru.vlsu.ispi.movieproject.security.CustomUserDetails;
import ru.vlsu.ispi.movieproject.service.CompilationService;

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
        return compilationService.getAll(PageRequest.of(page, size), user != null ? user.getId() : null);
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/{id}")
    public CompilationDto getCompilation(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails user) {
        return compilationService.getById(id, user != null ? user.getId() : null);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CompilationDto createCompilation(@ModelAttribute CreateCompilationRequest request, @AuthenticationPrincipal CustomUserDetails user) {
        return compilationService.createCompilation(request, user.getId());
    }

    @DeleteMapping("/{id}")
    public void deleteCompilation(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails user) {
        compilationService.deleteCompilation(id, user.getId());
    }

    @PostMapping("/{id}/like")
    public void likeCompilation(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails user) {
        compilationService.like(id, user.getId());
    }

    @DeleteMapping("/{id}/like")
    public void unlikeCompilation(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails user) {
        compilationService.unlike(id, user.getId());
    }

}
