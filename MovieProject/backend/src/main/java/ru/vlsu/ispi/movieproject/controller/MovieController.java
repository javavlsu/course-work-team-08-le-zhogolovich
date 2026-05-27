package ru.vlsu.ispi.movieproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import ru.vlsu.ispi.movieproject.dto.movie.SearchMoviesRequest;
import ru.vlsu.ispi.movieproject.dto.tag.TagDto;
import ru.vlsu.ispi.movieproject.service.MovieService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/movies")
public class MovieController {
    private final MovieService movieService;

    @GetMapping()
    public Page<MovieDto> getMovies(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Long genreId,
            @RequestParam(required = false) Long tagId,
            @RequestParam(required = false) Long countryId,
            @RequestParam(required = false) Integer year,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder,

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {

        SearchMoviesRequest request = new SearchMoviesRequest();
        request.setQuery(query);
        request.setGenreId(genreId);
        request.setTagId(tagId);
        request.setCountryId(countryId);
        request.setYear(year);
        request.setSortBy(sortBy);
        request.setSortOrder(sortOrder);

        return movieService.searchMovies(request, page, size);
    }

    @GetMapping("/{id}")
    public MovieFullDto getMovie(@PathVariable Long id) {
        return movieService.getMovie(id);
    }

    @PostMapping("/{id}/compilations")
    public void addMovieToCompilation(@PathVariable Long id, @RequestBody @Valid AddMovieToCompilationsRequest request) {
        movieService.addMovieToCompilations(id, request.getCompilationIds());
    }

    @PostMapping("/{id}/rating")
    public void rateMovie(@PathVariable Long id, @RequestBody @Valid RateMovieRequest request) {
        movieService.rateMovie(id, request.getRating());
    }

    @PostMapping("/{movieId}/tags/{tagId}")
    public void addTagToMovie(@PathVariable Long movieId, @PathVariable Long tagId) {
        movieService.addTag(movieId, tagId);
    }

    @DeleteMapping("/{movieId}/tags/{tagId}")
    public void removeTagFromMovie(@PathVariable Long movieId, @PathVariable Long tagId) {
        movieService.removeTag(movieId, tagId);
    }

    @GetMapping("/{movieId}/tags")
    public List<TagDto> getMovieTags(@PathVariable Long movieId) {
        return movieService.getMovieTags(movieId);
    }

    @GetMapping("/top/top10")
    public List<MovieDto> getTop10Movies() {
        return movieService.getTop10Movies();
    }
}
