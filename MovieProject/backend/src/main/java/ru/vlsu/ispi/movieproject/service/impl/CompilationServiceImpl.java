package ru.vlsu.ispi.movieproject.service.impl;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.vlsu.ispi.movieproject.dto.compilation.CompilationDto;
import ru.vlsu.ispi.movieproject.dto.compilation.CreateCompilationRequest;
import ru.vlsu.ispi.movieproject.dto.compilation.UpdateCompilationRequest;
import ru.vlsu.ispi.movieproject.dto.movie.MovieDto;
import ru.vlsu.ispi.movieproject.enums.FileDirectory;
import ru.vlsu.ispi.movieproject.exception.CompilationNotFoundException;
import ru.vlsu.ispi.movieproject.exception.MovieIsNotInCompilationException;
import ru.vlsu.ispi.movieproject.exception.UserNotFoundException;
import ru.vlsu.ispi.movieproject.mapper.CompilationMapper;
import ru.vlsu.ispi.movieproject.mapper.MovieMapper;
import ru.vlsu.ispi.movieproject.model.Compilation;
import ru.vlsu.ispi.movieproject.model.CompilationLike;
import ru.vlsu.ispi.movieproject.model.CompilationLikeId;
import ru.vlsu.ispi.movieproject.model.CompilationSubscription;
import ru.vlsu.ispi.movieproject.model.User;
import ru.vlsu.ispi.movieproject.projection.CompilationProjection;
import ru.vlsu.ispi.movieproject.repository.CompilationLikeRepository;
import ru.vlsu.ispi.movieproject.repository.CompilationRepository;
import ru.vlsu.ispi.movieproject.repository.CompilationSubscriptionRepository;
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
    private final CompilationSubscriptionRepository compilationSubscriptionRepository;
    private final MovieMapper movieMapper;

    @Override
    public CompilationDto createCompilation(CreateCompilationRequest request) {
        Long userId = currentUserService.getCurrentUserID();

        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new UserNotFoundException());

        Compilation compilation = compilationMapper.fromRequest(request);
        compilation.setAuthor(user);
        compilationRepository.save(compilation);

        return buildDto(compilation, userId);
    }

    @Override
    public CompilationDto editCompilation(Long id, UpdateCompilationRequest request) {
        Long userId = currentUserService.getCurrentUserID();

        Compilation compilation = compilationRepository.findById(id)
                .orElseThrow(() -> new CompilationNotFoundException(id));
        checkOwner(compilation, userId);

        if (request.getTitle() != null) {
            compilation.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            compilation.setDescription(request.getDescription());
        }
        if (request.getIsPublic() != null) {
            compilation.setIsPublic(request.getIsPublic());
        }

        return buildDto(compilation, userId);
    }

    @Override
    public CompilationDto updateCover(Long id, MultipartFile cover) {
        Long userId = currentUserService.getCurrentUserID();

        Compilation compilation = compilationRepository.findById(id)
                .orElseThrow(() -> new CompilationNotFoundException(id));
        checkOwner(compilation, userId);

        String coverUrl = fileStorageService.upload(cover, FileDirectory.COMPILATIONS.getFolder());

        fileStorageService.delete(compilation.getCoverUrl());
        compilation.setCoverUrl(coverUrl);

        return buildDto(compilation, userId);
    }

    @Override
    public void deleteCompilation(Long id) {
        Long userId = currentUserService.getCurrentUserID();

        Compilation compilation = compilationRepository.findById(id)
                .orElseThrow(() -> new CompilationNotFoundException(id));

        checkOwner(compilation, userId);

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

        return buildDto(compilation, userId);
    }

    @Override
    public Page<CompilationDto> getAll(Pageable pageable) {
        Long userId = currentUserService.getCurrentUserID();

        return compilationRepository.findAllView(pageable, userId)
                .map(p -> compilationMapper.fromView(p, List.of()));
    }

    @Override
    public CompilationDto removeMovie(Long compilationId, Long movieId) {
        Long userId = currentUserService.getCurrentUserID();

        Compilation compilation = compilationRepository.findById(compilationId)
                .orElseThrow(() -> new CompilationNotFoundException(compilationId));
        checkOwner(compilation, userId);

        boolean removed = compilation.getMovies().removeIf(movie -> movie.getId().equals(movieId));
        if (!removed) {
            throw new MovieIsNotInCompilationException();
        }

        return buildDto(compilation, userId);
    }

    @Override
    public List<CompilationDto> getCurrentUserCompilations() {
        Long userId = currentUserService.getCurrentUserID();

        return compilationRepository.findAllByAuthorId(userId, userId)
                .stream()
                .map(p -> compilationMapper.fromView(p, List.of()))
                .toList();
    }

    @Override
    public List<CompilationDto> getUserCompilations(Long userId) {
        Long currentUserId = currentUserService.getCurrentUserID();

        return compilationRepository.findAllByAuthorId(userId, currentUserId)
                .stream()
                .map(p -> compilationMapper.fromView(p, List.of()))
                .toList();
    }

    @Override
    public List<CompilationDto> getCurrentUserSubscriptions() {
        Long userId = currentUserService.getCurrentUserID();

        return compilationRepository.findAllSubscribedByUserId(userId, userId)
                .stream()
                .map(p -> compilationMapper.fromView(p, List.of()))
                .toList();
    }

    @Override
    public List<CompilationDto> getUserSubscriptions(Long userId) {
        Long currentUserId = currentUserService.getCurrentUserID();

        return compilationRepository.findAllSubscribedByUserId(userId, currentUserId)
                .stream()
                .map(p -> compilationMapper.fromView(p, List.of()))
                .toList();
    }


    @Override
    public void subscribe(Long compilationId) {
        Long userId = currentUserService.getCurrentUserID();

        Compilation compilation = compilationRepository.findById(compilationId)
                .orElseThrow(() -> new CompilationNotFoundException(compilationId));

        if (compilation.getAuthor() != null && compilation.getAuthor().getId().equals(userId)) {
            throw new IllegalStateException("Нельзя подписать на свою подборку");
        }

        boolean exists = compilationSubscriptionRepository.existsByUserIdAndCompilationId(userId, compilationId);
        if (exists) {
            throw new IllegalStateException("Уже подписан");
        }

        CompilationSubscription compilationSubscription = new CompilationSubscription();
        compilationSubscription.setUser(entityManager.getReference(User.class, userId));
        compilationSubscription.setCompilation(entityManager.getReference(Compilation.class, compilationId));
        compilationSubscriptionRepository.save(compilationSubscription);
    }

    @Override
    public void unsubscribe(Long compilationId) {
        Long userId = currentUserService.getCurrentUserID();

        CompilationSubscription compilationSubscription = compilationSubscriptionRepository
                .findByUserIdAndCompilationId(userId, compilationId)
                .orElseThrow(() -> new IllegalStateException("Вы не подписаны на эту подборку"));

        compilationSubscriptionRepository.delete(compilationSubscription);
    }

    private CompilationProjection getView(Long id, Long userId) {
        return compilationRepository.findViewById(id, userId)
                .orElseThrow(() -> new CompilationNotFoundException(id));
    }

    private void checkOwner(Compilation compilation, Long userId) {
        if (compilation.getAuthor() == null || !compilation.getAuthor().getId().equals(userId) ) {
            throw new AccessDeniedException("Вы не являетесь владельцем подборки");
        }
    }

    private CompilationDto buildDto(Compilation compilation, Long userId) {
        CompilationProjection projection = getView(compilation.getId(), userId);

        List<MovieDto> movies = compilation.getMovies().stream().map(movieMapper::toMovieDto).toList();
        return compilationMapper.fromView(projection, movies);
    }
}
