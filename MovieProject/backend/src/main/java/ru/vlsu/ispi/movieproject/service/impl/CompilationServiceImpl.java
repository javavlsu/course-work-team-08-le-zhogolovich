package ru.vlsu.ispi.movieproject.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.vlsu.ispi.movieproject.dto.compilation.CompilationDto;
import ru.vlsu.ispi.movieproject.dto.compilation.CreateCompilationRequest;
import ru.vlsu.ispi.movieproject.dto.compilation.UpdateCompilationRequest;
import ru.vlsu.ispi.movieproject.enums.FileDirectory;
import ru.vlsu.ispi.movieproject.exception.CompilationNotFoundException;
import ru.vlsu.ispi.movieproject.exception.MovieAlreadyInCompilationException;
import ru.vlsu.ispi.movieproject.exception.MovieIsNotInCompilationException;
import ru.vlsu.ispi.movieproject.exception.UserNotFoundException;
import ru.vlsu.ispi.movieproject.mapper.CompilationMapper;
import ru.vlsu.ispi.movieproject.model.Compilation;
import ru.vlsu.ispi.movieproject.model.CompilationLike;
import ru.vlsu.ispi.movieproject.model.CompilationLikeId;
import ru.vlsu.ispi.movieproject.model.Movie;
import ru.vlsu.ispi.movieproject.model.User;
import ru.vlsu.ispi.movieproject.projection.CompilationProjection;
import ru.vlsu.ispi.movieproject.projection.CompilationStatsProjection;
import ru.vlsu.ispi.movieproject.repository.CompilationLikeRepository;
import ru.vlsu.ispi.movieproject.repository.CompilationRepository;
import ru.vlsu.ispi.movieproject.repository.UserRepository;
import ru.vlsu.ispi.movieproject.service.CompilationService;
import ru.vlsu.ispi.movieproject.service.CurrentUserService;
import ru.vlsu.ispi.movieproject.service.FileStorageService;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final UserRepository userRepository;
    private final EntityManager entityManager;
    private final CompilationMapper compilationMapper;
    private final CompilationRepository compilationRepository;
    private final CompilationLikeRepository compilationLikeRepository;
    private final FileStorageService fileStorageService;
    private final CurrentUserService currentUserService;

    @Override
    public CompilationDto createCompilation(CreateCompilationRequest request) {
        Long userId = currentUserService.getCurrentUserID();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        Compilation compilation = compilationMapper.fromRequest(request);
        compilation.setAuthor(user);

        return compilationMapper.toDto(compilationRepository.save(compilation), 0L, false);
    }

    @Override
    public CompilationDto editCompilation(Long id, UpdateCompilationRequest request) {
        Long userId = currentUserService.getCurrentUserID();

        Compilation compilation = compilationRepository.findById(id)
                .orElseThrow(() -> new CompilationNotFoundException(id));
        if (!compilation.getAuthor().getId().equals(userId)) {
            throw new AccessDeniedException("Вы не являетесь владельцем подборки");
        }

        if (request.getTitle() != null) {
            compilation.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            compilation.setDescription(request.getDescription());
        }
        if (request.getIsPublic() != null) {
            compilation.setIsPublic(request.getIsPublic());
        }
        MultipartFile cover = request.getCover();
        if (cover != null && !cover.isEmpty()) {
            String coverUrl = fileStorageService.upload(cover, FileDirectory.COMPILATIONS.getFolder());
            compilation.setCoverUrl(coverUrl);
        }

        CompilationStatsProjection projection = compilationRepository.getStats(id, userId);

        return compilationMapper.toDto(compilationRepository.save(compilation), projection.getLikesCount(), projection.getLikedByUser());
    }

    @Override
    public void deleteCompilation(Long id) {
        Long userId = currentUserService.getCurrentUserID();

        Compilation compilation = compilationRepository.findById(id)
                .orElseThrow(() -> new CompilationNotFoundException(id));

        if (!compilation.getAuthor().getId().equals(userId)) {
            throw new AccessDeniedException("Доступ запрещён");
        }
        compilationRepository.delete(compilation);
    }

    @Override
    public void like(Long compilationId) {
        Long userId = currentUserService.getCurrentUserID();

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
    public void unlike(Long compilationId) {
        Long userId = currentUserService.getCurrentUserID();

        compilationLikeRepository.deleteByUserIdAndCompilationId(userId, compilationId);
    }

    @Override
    public CompilationDto getById(Long id) {
        Long userId = currentUserService.getCurrentUserID();

        Compilation compilation = compilationRepository.findByIdWithMovies(id)
                .orElseThrow(() -> new CompilationNotFoundException(id));

        CompilationStatsProjection projection = compilationRepository.getStats(id, userId);

        return compilationMapper.toDto(compilation, projection.getLikesCount(), projection.getLikedByUser());
    }

    @Override
    public Page<CompilationDto> getAll(Pageable pageable) {
        Long userId = currentUserService.getCurrentUserID();

        Page<CompilationProjection> page = compilationRepository.findAllWithLikes(pageable, userId);
        return page.map(compilationMapper::fromProjection);
    }

    @Override
    public void addMovie(Long compilationId, Long movieId) {
        Long userId = currentUserService.getCurrentUserID();

        Compilation compilation = compilationRepository.findById(compilationId)
                .orElseThrow(() -> new CompilationNotFoundException(compilationId));
        if (!compilation.getAuthor().getId().equals(userId)) {
            throw new AccessDeniedException("Вы не являетесь владельцем подборки");
        }

        Movie movie = entityManager.getReference(Movie.class, movieId);
        if (compilation.getMovies().contains(movie)) {
            throw new MovieAlreadyInCompilationException();
        }
        compilation.getMovies().add(movie);
    }

    @Override
    public CompilationDto removeMovie(Long compilationId, Long movieId) {
        Long userId = currentUserService.getCurrentUserID();

        Compilation compilation = compilationRepository.findById(compilationId)
                .orElseThrow(() -> new CompilationNotFoundException(compilationId));
        if (!compilation.getAuthor().getId().equals(userId)) {
            throw new AccessDeniedException("Вы не являетесь владельцем подборки");
        }

        boolean removed = compilation.getMovies().removeIf(movie -> movie.getId().equals(movieId));
        if (!removed) {
            throw new MovieIsNotInCompilationException();
        }

        CompilationStatsProjection stats = compilationRepository.getStats(compilationId, userId);

        return compilationMapper.toDto(compilation, stats.getLikesCount(), stats.getLikedByUser());
    }

    @Override
    public List<CompilationDto> getUserCompilations() {
        Long userId = currentUserService.getCurrentUserID();

        return compilationRepository.findAllByAuthorId(userId)
                .stream()
                .map(c -> compilationMapper.toDto(c, 0L, true))
                .toList();
    }
}
