package ru.vlsu.ispi.movieproject.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.vlsu.ispi.movieproject.dto.compilation.CompilationDto;
import ru.vlsu.ispi.movieproject.dto.compilation.CreateCompilationRequest;

public interface CompilationService {
    CompilationDto createCompilation(CreateCompilationRequest request, Long userId);
    void deleteCompilation(Long id, Long userId);
    void like(Long compilationId, Long userId);
    void unlike(Long compilationId, Long userId);
    CompilationDto getById(Long id, Long userId);
    Page<CompilationDto> getAll(Pageable pageable, Long userId);
}
