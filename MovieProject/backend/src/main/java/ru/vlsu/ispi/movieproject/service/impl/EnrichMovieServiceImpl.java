package ru.vlsu.ispi.movieproject.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vlsu.ispi.movieproject.dto.imports.ExternalSourceDto;
import ru.vlsu.ispi.movieproject.dto.imports.ExternalSourcesResponseDto;
import ru.vlsu.ispi.movieproject.dto.movie.MovieDetailsDto;
import ru.vlsu.ispi.movieproject.exception.MovieNotFoundException;
import ru.vlsu.ispi.movieproject.model.ExternalSource;
import ru.vlsu.ispi.movieproject.model.Movie;
import ru.vlsu.ispi.movieproject.repository.MovieRepository;
import ru.vlsu.ispi.movieproject.service.EnrichMovieService;
import ru.vlsu.ispi.movieproject.service.KinopoiskApiService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EnrichMovieServiceImpl implements EnrichMovieService {
    private final KinopoiskApiService kinopoiskApiService;
    private final MovieRepository movieRepository;

    @Value("${movie.details.duration}")
    private Duration detailsDuration;

    @Transactional
    public boolean enrichMovie(Long id, LocalDateTime now) {
        Movie movie = movieRepository.findById(id).
                orElseThrow(() -> new MovieNotFoundException(id));

        if (movie.getDetailsLoadedAt() != null &&
                !movie.getDetailsLoadedAt().isBefore(now.minus(detailsDuration))) {
            return false;
        }

        enrichMovieInternal(movie);
        return true;
    }

    public void enrichMovieInternal(Movie movie) {
        MovieDetailsDto details = kinopoiskApiService.getMovieDetails(movie.getKinopoiskId());
        movie.setOverview(details.getDescription());

        ExternalSourcesResponseDto sources = kinopoiskApiService.getExternalSources(movie.getKinopoiskId());
        movie.getExternalSources().clear();

        Set<String> newUrls = new HashSet<>();

        if (sources != null && sources.getItems() != null) {
            for (ExternalSourceDto dto : sources.getItems()){
                String url = normalizeUrl(dto.getUrl());
                if (url == null || !newUrls.add(url)) continue;

                ExternalSource externalSource = new ExternalSource();
                externalSource.setUrl(dto.getUrl());
                externalSource.setPlatform(dto.getPlatform());
                externalSource.setLogoUrl(dto.getLogoUrl());
                externalSource.setMovie(movie);

                movie.getExternalSources().add(externalSource);
            }
        }

        movie.setDetailsLoadedAt(LocalDateTime.now());
    }

    private String normalizeUrl(String url) {
        if (url == null) return null;

        int index = url.indexOf("?");
        return index > 0 ? url.substring(0, index) : url;
    }
}
