package ru.vlsu.ispi.movieproject.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.vlsu.ispi.movieproject.dto.imports.ExternalSourceDto;
import ru.vlsu.ispi.movieproject.dto.imports.ExternalSourcesResponseDto;
import ru.vlsu.ispi.movieproject.dto.movie.MovieDetailsDto;
import ru.vlsu.ispi.movieproject.dto.movie.MovieDto;
import ru.vlsu.ispi.movieproject.dto.movie.MovieFullDto;
import ru.vlsu.ispi.movieproject.exception.MovieNotFoundException;
import ru.vlsu.ispi.movieproject.mapper.MovieMapper;
import ru.vlsu.ispi.movieproject.model.ExternalSource;
import ru.vlsu.ispi.movieproject.model.Movie;
import ru.vlsu.ispi.movieproject.repository.MovieRatingRepository;
import ru.vlsu.ispi.movieproject.repository.MovieRepository;
import ru.vlsu.ispi.movieproject.service.CurrentUserService;
import ru.vlsu.ispi.movieproject.service.KinopoiskApiService;
import ru.vlsu.ispi.movieproject.service.MovieService;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {
    private final MovieRepository movieRepository;
    private final KinopoiskApiService kinopoiskApiService;
    private final MovieRatingRepository movieRatingRepository;
    private final MovieMapper movieMapper;
    private final CurrentUserService currentUserService;

    @Value("${movie.details.duration}")
    private Duration detailsDuration;

    @Override
    public Page<MovieDto> getAllMovies(Pageable pageable) {
        return movieRepository.findAll(pageable).map(movieMapper::toMovieDto);
    }

    @Override
    @Transactional
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


}
