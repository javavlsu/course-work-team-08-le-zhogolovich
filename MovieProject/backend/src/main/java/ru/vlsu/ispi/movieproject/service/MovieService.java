package ru.vlsu.ispi.movieproject.service;

import org.springframework.data.domain.Page;
import ru.vlsu.ispi.movieproject.dto.movie.MovieDto;
import ru.vlsu.ispi.movieproject.dto.movie.MovieFullDto;
import ru.vlsu.ispi.movieproject.dto.movie.SearchMoviesRequest;
import ru.vlsu.ispi.movieproject.dto.tag.TagDto;

import java.util.List;

public interface MovieService {
    Page<MovieDto> searchMovies(SearchMoviesRequest request, int page, int size);
    MovieFullDto getMovie(Long id);
    void addMovieToCompilations(Long id, List<Long> compilationIds);
    void rateMovie(Long id, Double rating);
    void addTag(Long id, Long tagId);
    void removeTag(Long id, Long tagId);
    List<TagDto> getMovieTags(Long id);
    List<MovieDto> getTop10Movies();
}
