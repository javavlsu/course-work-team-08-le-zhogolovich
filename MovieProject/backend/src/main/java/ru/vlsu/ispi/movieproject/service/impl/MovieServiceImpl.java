package ru.vlsu.ispi.movieproject.service.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vlsu.ispi.movieproject.dto.imports.ExternalSourceDto;
import ru.vlsu.ispi.movieproject.dto.imports.ExternalSourcesResponseDto;
import ru.vlsu.ispi.movieproject.dto.movie.MovieDetailsDto;
import ru.vlsu.ispi.movieproject.dto.movie.MovieFullDto;
import ru.vlsu.ispi.movieproject.dto.movie.MovieImportDto;
import ru.vlsu.ispi.movieproject.exception.MovieNotFoundException;
import ru.vlsu.ispi.movieproject.mapper.MovieMapper;
import ru.vlsu.ispi.movieproject.model.ExternalSource;
import ru.vlsu.ispi.movieproject.model.Movie;
import ru.vlsu.ispi.movieproject.repository.MovieRatingRepository;
import ru.vlsu.ispi.movieproject.repository.MovieRepository;
import ru.vlsu.ispi.movieproject.service.KinopoiskApiService;
import ru.vlsu.ispi.movieproject.service.MovieService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class MovieServiceImpl implements MovieService {
    private final MovieRepository movieRepository;
    private final KinopoiskApiService kinopoiskApiService;
    private final MovieRatingRepository movieRatingRepository;
    private final MovieMapper movieMapper;

    private final Duration DETAILS_DURATION = Duration.ofDays(30);

    @Override
    public List<MovieImportDto> getAllMovies() {
        return null;
//                movieRepository.findAll().stream().map(MovieMapper::toDto).toList();
    }

    @Override
    @Transactional
    public MovieFullDto getMovie(Long id, Long userId) {
        Movie movie = movieRepository.findByIdForUpdate(id).orElseThrow(() -> new MovieNotFoundException(id));

        if (movie.getDetailsLoadedAt() == null || movie.getDetailsLoadedAt().isBefore(LocalDateTime.now().minus(DETAILS_DURATION))) {
            enrichMovie(movie);
        }

        MovieFullDto dto = movieMapper.toFullDto(movie);

        if (userId != null) {
            movieRatingRepository.getUserRating(id, userId).ifPresent(dto::setMyRating);
        }

        return dto;
    }

    @Override
    @Transactional
    public void enrichMovie(Movie movie) {
        MovieDetailsDto details = kinopoiskApiService.getMovieDetails(movie.getKinopoiskId());

        movie.setOverview(details.getDescription());
        loadExternalSources(movie);
        movie.setDetailsLoadedAt(LocalDateTime.now());

        movieRepository.save(movie);
    }

    @Override
    @Transactional
    public void loadExternalSources(Movie movie) {
        ExternalSourcesResponseDto sources = kinopoiskApiService.getExternalSources(movie.getKinopoiskId());
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


}
