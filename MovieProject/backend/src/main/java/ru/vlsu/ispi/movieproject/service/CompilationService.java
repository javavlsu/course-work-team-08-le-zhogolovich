package ru.vlsu.ispi.movieproject.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import ru.vlsu.ispi.movieproject.dto.compilation.CompilationDto;
import ru.vlsu.ispi.movieproject.dto.compilation.CreateCompilationRequest;
import ru.vlsu.ispi.movieproject.dto.compilation.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    CompilationDto createCompilation(CreateCompilationRequest request);
    CompilationDto editCompilation(Long id, UpdateCompilationRequest request);
    CompilationDto updateCover(Long id, MultipartFile cover);
    void deleteCompilation(Long id);
    void like(Long compilationId);
    void unlike(Long compilationId);
    CompilationDto getById(Long id);
    Page<CompilationDto> getAll(Pageable pageable);
    CompilationDto removeMovie(Long compilationId, Long movieId);
    List<CompilationDto> getCurrentUserCompilations();
    List<CompilationDto> getUserCompilations(Long userId);
    void subscribe(Long compilationId);
    void unsubscribe(Long compilationId);
}
