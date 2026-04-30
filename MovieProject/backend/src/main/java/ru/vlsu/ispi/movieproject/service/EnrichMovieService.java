package ru.vlsu.ispi.movieproject.service;

import java.time.LocalDateTime;

public interface EnrichMovieService {
    boolean enrichMovie(Long id, LocalDateTime now);
}
