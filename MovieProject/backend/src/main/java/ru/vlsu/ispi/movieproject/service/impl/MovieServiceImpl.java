package ru.vlsu.ispi.movieproject.service.impl;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vlsu.ispi.movieproject.dto.movie.MovieDto;
import ru.vlsu.ispi.movieproject.dto.movie.MovieFullDto;
import ru.vlsu.ispi.movieproject.dto.movie.SearchMoviesRequest;
import ru.vlsu.ispi.movieproject.dto.tag.TagDto;
import ru.vlsu.ispi.movieproject.exception.CompilationNotFoundException;
import ru.vlsu.ispi.movieproject.exception.MovieNotFoundException;
import ru.vlsu.ispi.movieproject.exception.TagNotFoundException;
import ru.vlsu.ispi.movieproject.mapper.MovieMapper;
import ru.vlsu.ispi.movieproject.mapper.TagMapper;
import ru.vlsu.ispi.movieproject.model.Compilation;
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
import ru.vlsu.ispi.movieproject.service.MovieService;
import ru.vlsu.ispi.movieproject.specification.MovieSpecifications;
import ru.vlsu.ispi.movieproject.util.FillTopUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MovieServiceImpl implements MovieService {
    private final MovieRepository movieRepository;
    private final MovieRatingRepository movieRatingRepository;
    private final MovieMapper movieMapper;
    private final CurrentUserService currentUserService;
    private final EntityManager entityManager;
    private final CompilationRepository compilationRepository;
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    @Override
    public Page<MovieDto> searchMovies(SearchMoviesRequest request, int page, int size) {
        Specification<Movie> spec = Specification.where(null);
        spec = spec.and(MovieSpecifications.hasGenre(request.getGenreId()));
        spec = spec.and(MovieSpecifications.hasTag(request.getTagId()));
        spec = spec.and(MovieSpecifications.hasCountry(request.getCountryId()));
        spec = spec.and(MovieSpecifications.hasYear(request.getYear()));
        spec = spec.and(MovieSpecifications.titleContains(request.getQuery()));
        spec = spec.and(MovieSpecifications.orderByName(request.getSortOrder()));

        Pageable pageable;
        if ("name".equalsIgnoreCase(request.getSortBy())) {
            pageable = PageRequest.of(page, size);
        } else {
            pageable = PageRequest.of(page, size, buildSort(request));
        }

        Page<Movie> moviePage = movieRepository.findAll(spec, pageable);

        List<Long> ids = moviePage.getContent().stream().map(Movie::getId).toList();
        if (ids.isEmpty()) return Page.empty(pageable);

        List<Movie> movies = movieRepository.findAllWithRelations(ids);

        Map<Long, Movie> moviesById = movies.stream().collect(Collectors.toMap(Movie::getId, m -> m));

        List<MovieDto> orderedList = ids.stream().map(moviesById::get).filter(Objects::nonNull)
                .map(movieMapper::toMovieDto).toList();

        return new PageImpl<>(orderedList, pageable, moviePage.getTotalElements());
    }

    @Override
    public MovieFullDto getMovie(Long id) {
        Long userId = currentUserService.getCurrentUserID();

        if (id == null) {
            throw new IllegalArgumentException("Id фильма не может быть пустым");
        }
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException(id));

        MovieFullDto dto = movieMapper.toFullDto(movie);

        if (userId != null) {
            movieRatingRepository.getUserRating(id, userId).ifPresent(dto::setMyRating);
        }

        return dto;
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

        return movie.getTags().stream().map(tagMapper::toDto).toList();
    }

    @Override
    public List<MovieDto> getTop10Movies() {
        LocalDateTime fromDate = LocalDateTime.now().minusDays(7);

        List<Movie> top = movieRepository.findTopMoviesLastWeek(fromDate, 8.0, PageRequest.of(0, 10));

        List<Movie> fullTop = FillTopUtil.fillTop(top, 10,
                excludeIds -> movieRepository.findLatest(excludeIds, PageRequest.of(0, 10 - top.size())),
                Movie::getId);

        return fullTop.stream().map(movieMapper::toMovieDto).toList();
    }

    private void updateMovieRating(Movie movie) {
        Double avg = movieRatingRepository.getAverageRating(movie.getId());
        Integer count = movieRatingRepository.getRatingsCount(movie.getId());

        movie.setAvgRating(avg != null ? avg : 0.0);
        movie.setRatingsCount(count != null ? count : 0);
    }

    private Sort buildSort(SearchMoviesRequest request) {
        String sortBy = request.getSortBy();
        String direction = request.getSortOrder();

        Sort.Direction dir = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;

        if ("year".equalsIgnoreCase(sortBy)) {
            return Sort.by(dir, "releaseYear");
        }

        if ("popularity".equalsIgnoreCase(sortBy)) {
            return Sort.by(
                    new Sort.Order(dir, "avgRating"),
                    new Sort.Order(dir, "ratingsCount")
            );
        }

        return Sort.by(Sort.Direction.DESC, "id");
    }
}
