package ru.vlsu.ispi.movieproject.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.vlsu.ispi.movieproject.dto.compilation.CompilationDto;
import ru.vlsu.ispi.movieproject.dto.compilation.CreateCompilationRequest;
import ru.vlsu.ispi.movieproject.dto.compilation.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    CompilationDto createCompilation(CreateCompilationRequest request);
    CompilationDto editCompilation(Long id, UpdateCompilationRequest request);
    void deleteCompilation(Long id);
    void like(Long compilationId);
    void unlike(Long compilationId);
    CompilationDto getById(Long id);
    Page<CompilationDto> getAll(Pageable pageable);
    void addMovie (Long compilationId, Long movieId);
    CompilationDto removeMovie(Long compilationId, Long movieId);
    List<CompilationDto> getUserCompilations();
}
