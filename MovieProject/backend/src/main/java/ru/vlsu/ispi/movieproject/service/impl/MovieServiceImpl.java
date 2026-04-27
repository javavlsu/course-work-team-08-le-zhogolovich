package ru.vlsu.ispi.movieproject.service.impl;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vlsu.ispi.movieproject.dto.imports.ExternalSourceDto;
import ru.vlsu.ispi.movieproject.dto.imports.ExternalSourcesResponseDto;
import ru.vlsu.ispi.movieproject.dto.movie.MovieDetailsDto;
import ru.vlsu.ispi.movieproject.dto.movie.MovieDto;
import ru.vlsu.ispi.movieproject.dto.movie.MovieFullDto;
import ru.vlsu.ispi.movieproject.dto.tag.TagDto;
import ru.vlsu.ispi.movieproject.exception.CompilationNotFoundException;
import ru.vlsu.ispi.movieproject.exception.MovieNotFoundException;
import ru.vlsu.ispi.movieproject.exception.TagNotFoundException;
import ru.vlsu.ispi.movieproject.mapper.MovieMapper;
import ru.vlsu.ispi.movieproject.mapper.TagMapper;
import ru.vlsu.ispi.movieproject.model.Compilation;
import ru.vlsu.ispi.movieproject.model.ExternalSource;
import ru.vlsu.ispi.movieproject.model.Movie;
import ru.vlsu.ispi.movieproject.model.MovieRating;
import ru.vlsu.ispi.movieproject.model.MovieRatingId;
import ru.vlsu.ispi.movieproject.model.Tag;
import ru.vlsu.ispi.movieproject.model.User;
import ru.vlsu.ispi.movieproject.repository.CompilationRepository;
import ru.vlsu.ispi.movieproject.repository.MovieRatingRepository;
import ru.vlsu.ispi.movieproject.repository.MovieRepository;
import ru.vlsu.ispi.movieproject.repository.TagRepository;
import ru.vlsu.ispi.movieproject.service.CurrentUserService;
import ru.vlsu.ispi.movieproject.service.KinopoiskApiService;
import ru.vlsu.ispi.movieproject.service.MovieService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MovieServiceImpl implements MovieService {
    private final MovieRepository movieRepository;
    private final KinopoiskApiService kinopoiskApiService;
    private final MovieRatingRepository movieRatingRepository;
    private final MovieMapper movieMapper;
    private final CurrentUserService currentUserService;
    private final EntityManager entityManager;
    private final CompilationRepository compilationRepository;
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    @Value("${movie.details.duration}")
    private Duration detailsDuration;

    @Override
    public Page<MovieDto> getAllMovies(Pageable pageable) {
        return movieRepository.findAll(pageable).map(movieMapper::toMovieDto);
    }

    @Override
    public MovieFullDto getMovie(Long id) {
        Long userId = currentUserService.getCurrentUserID();

        if (id == null) {
            throw new IllegalArgumentException("Id фильма не может быть пустым");
        }
        Movie movie = movieRepository.findByIdForUpdate(id).orElseThrow(() -> new MovieNotFoundException(id));

        if (movie.getDetailsLoadedAt() == null || movie.getDetailsLoadedAt().isBefore(LocalDateTime.now().minus(detailsDuration))) {
            enrichMovie(movie);
        }

        MovieFullDto dto = movieMapper.toFullDto(movie);

        if (userId != null) {
            movieRatingRepository.getUserRating(id, userId).ifPresent(dto::setMyRating);
        }

        return dto;
    }

    @Override
    public void enrichMovie(Movie movie) {
        MovieDetailsDto details = kinopoiskApiService.getMovieDetails(movie.getKinopoiskId());

        movie.setOverview(details.getDescription());
        loadExternalSources(movie);
        movie.setDetailsLoadedAt(LocalDateTime.now());
    }

    @Override
    public void loadExternalSources(Movie movie) {
        ExternalSourcesResponseDto sources = kinopoiskApiService.getExternalSources(movie.getKinopoiskId());
        if (sources == null || sources.getItems() == null) {
            return;
        }
        movie.getExternalSources().clear();

        for (ExternalSourceDto dto : sources.getItems()){
            ExternalSource externalSource = new ExternalSource();

            externalSource.setUrl(dto.getUrl());
            externalSource.setPlatform(dto.getPlatform());
            externalSource.setLogoUrl(dto.getLogoUrl());
            externalSource.setMovie(movie);

            movie.getExternalSources().add(externalSource);
        }
    }

    @Override
    public void addMovieToCompilations(Long id, List<Long> compilationIds) {
        Long userId = currentUserService.getCurrentUserID();

        Movie movie = entityManager.getReference(Movie.class, id);

        List<Compilation> compilations = compilationRepository.findAllById(compilationIds);

        if (compilations.size() != compilationIds.size()) {
            throw new CompilationNotFoundException();
        }

        for (Compilation compilation : compilations) {
            if (!compilation.getAuthor().getId().equals(userId)) {
                throw new AccessDeniedException("Нет доступа к одной из подборок");
            }
            if (compilation.getMovies().contains(movie)) {
                continue;
            }

            compilation.getMovies().add(movie);
        }
    }

    @Override
    public void rateMovie(Long id, Double rating) {
        Long userId = currentUserService.getCurrentUserID();

        if (rating == null || rating < 1 || rating > 10) {
            throw new IllegalArgumentException("Рейтинг должен быть от 1 до 10");
        }

        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException(id));

        MovieRatingId movieRatingId = new MovieRatingId(userId, id);
        MovieRating movieRating = movieRatingRepository.findById(movieRatingId).orElse(null);

        if (movieRating == null) {
            movieRating = new MovieRating();
            movieRating.setId(movieRatingId);
            movieRating.setMovie(movie);
            movieRating.setUser(entityManager.getReference(User.class, userId));
        }
        movieRating.setRating(rating);
        movieRatingRepository.save(movieRating);

        updateMovieRating(movie);
    }

    @Override
    public void addTag(Long id, Long tagId) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException(id));
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new TagNotFoundException(tagId));

        movie.getTags().add(tag);
    }

    @Override
    public void removeTag(Long id, Long tagId) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException(id));

        movie.getTags().removeIf(tag -> tag.getId().equals(tagId));
    }

    @Override
    public List<TagDto> getMovieTags(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException(id));

        return movie.getTags().stream().map(tagMapper::toDto).collect(Collectors.toList());
    }

    private void updateMovieRating(Movie movie) {
        Double avg = movieRatingRepository.getAverageRating(movie.getId());
        Integer count = movieRatingRepository.getRatingsCount(movie.getId());

        movie.setAvgRating(avg != null ? avg : 0.0);
        movie.setRatingsCount(count != null ? count : 0);
    }
}
