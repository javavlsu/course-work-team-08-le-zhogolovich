package ru.vlsu.ispi.movieproject.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.vlsu.ispi.movieproject.dto.movie.MovieDto;
import ru.vlsu.ispi.movieproject.service.MovieService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/movies")
@CrossOrigin(origins = "http://localhost:3000")
public class MovieController {
    private final MovieService movieService;

    @GetMapping()
    public List<MovieDto> getMovies() {
        return movieService.getAllMovies();
    }
}
