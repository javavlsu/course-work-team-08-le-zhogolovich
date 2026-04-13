package ru.vlsu.ispi.movieproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.vlsu.ispi.movieproject.dto.movie.AddMovieToCompilationsRequest;
import ru.vlsu.ispi.movieproject.dto.movie.MovieDto;
import ru.vlsu.ispi.movieproject.dto.movie.MovieFullDto;
import ru.vlsu.ispi.movieproject.dto.movie.RateMovieRequest;
import ru.vlsu.ispi.movieproject.service.MovieService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/movies")
public class MovieController {
    private final MovieService movieService;

    @GetMapping()
    public Page<MovieDto> getMovies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return movieService.getAllMovies(PageRequest.of(page, size));
    }

    @GetMapping("/{id}")
    public MovieFullDto getMovie(@PathVariable Long id) {
        return movieService.getMovie(id);
    }

    @PostMapping("/{id}/compilations")
    public void addMovieToCompilation(@PathVariable Long id, @RequestBody @Valid AddMovieToCompilationsRequest request) {
        movieService.addMovieToCompilations(id, request.getCompilationIds());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/rating")
    public void rateMovie(@PathVariable Long id, @RequestBody @Valid RateMovieRequest request) {
        movieService.rateMovie(id, request.getRating());
    }
}
