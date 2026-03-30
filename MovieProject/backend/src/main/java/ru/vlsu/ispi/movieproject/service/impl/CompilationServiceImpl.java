package ru.vlsu.ispi.movieproject.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import ru.vlsu.ispi.movieproject.dto.compilation.CompilationDto;
import ru.vlsu.ispi.movieproject.dto.compilation.CreateCompilationRequest;
import ru.vlsu.ispi.movieproject.exception.CompilationNotFoundException;
import ru.vlsu.ispi.movieproject.exception.UserNotFoundException;
import ru.vlsu.ispi.movieproject.mapper.CompilationMapper;
import ru.vlsu.ispi.movieproject.model.Compilation;
import ru.vlsu.ispi.movieproject.model.CompilationLike;
import ru.vlsu.ispi.movieproject.model.CompilationLikeId;
import ru.vlsu.ispi.movieproject.model.User;
import ru.vlsu.ispi.movieproject.projection.CompilationProjection;
import ru.vlsu.ispi.movieproject.projection.CompilationStatsProjection;
import ru.vlsu.ispi.movieproject.repository.CompilationLikeRepository;
import ru.vlsu.ispi.movieproject.repository.CompilationRepository;
import ru.vlsu.ispi.movieproject.repository.UserRepository;
import ru.vlsu.ispi.movieproject.service.CompilationService;

@Service
@Transactional
@RequiredArgsConstructor

public class CompilationServiceImpl implements CompilationService {
    private final UserRepository userRepository;
    private final EntityManager entityManager;
    private final CompilationMapper compilationMapper;
    private final CompilationRepository compilationRepository;
    private final CompilationLikeRepository compilationLikeRepository;

    @Override
    public CompilationDto createCompilation(CreateCompilationRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        Compilation compilation = compilationMapper.fromRequest(request);
        compilation.setAuthor(user);

        return compilationMapper.toDto(compilationRepository.save(compilation), 0L, false);
    }

    @Override
    public void deleteCompilation(Long id, Long userId) {
        Compilation compilation = compilationRepository.findById(id)
                .orElseThrow(() -> new CompilationNotFoundException(id));

        if (!compilation.getAuthor().getId().equals(userId)) {
            throw new AccessDeniedException("Доступ запрещён");
        }
        compilationRepository.delete(compilation);
    }

    @Override
    public void like(Long compilationId, Long userId) {
        if (compilationLikeRepository.existsByUserIdAndCompilationId(userId, compilationId)) {
            return;
        }

        CompilationLike compilationLike = new CompilationLike();
        compilationLike.setId(new CompilationLikeId(userId, compilationId));
        compilationLike.setUser(entityManager.getReference(User.class, userId));
        compilationLike.setCompilation(entityManager.getReference(Compilation.class, compilationId));

        compilationLikeRepository.save(compilationLike);
    }

    @Override
    public void unlike(Long compilationId, Long userId) {
        compilationLikeRepository.deleteByUserIdAndCompilationId(userId, compilationId);
    }

    @Override
    public CompilationDto getById(Long id, Long userId) {
        Compilation compilation = compilationRepository.findByIdWithMovies(id)
                .orElseThrow(() -> new CompilationNotFoundException(id));

        CompilationStatsProjection projection = compilationRepository.getStats(id, userId);

        return compilationMapper.toDto(compilation, projection.getLikesCount(), projection.getLikedByUser());
    }

    @Override
    public Page<CompilationDto> getAll(Pageable pageable, Long userId) {
        Page<CompilationProjection> page = compilationRepository.findAllWithLikes(pageable, userId);
        return page.map(compilationMapper::fromProjection);
    }
}
