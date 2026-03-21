package ru.vlsu.ispi.movieproject.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.vlsu.ispi.movieproject.dto.movie.MovieFullDto;
import ru.vlsu.ispi.movieproject.dto.movie.MovieImportDto;
import ru.vlsu.ispi.movieproject.model.User;
import ru.vlsu.ispi.movieproject.security.CustomUserDetails;
import ru.vlsu.ispi.movieproject.service.MovieService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/movies")
@CrossOrigin(origins = "http://localhost:3000")
public class MovieController {
    private final MovieService movieService;

    @GetMapping()
    public List<MovieImportDto> getMovies() {
        return movieService.getAllMovies();
    }

    @GetMapping("/{id}")
    public MovieFullDto getMovie(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails user) {
        return movieService.getMovie(id, user != null ? user.getId() : null);
    }
}
