package ru.vlsu.ispi.movieproject.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vlsu.ispi.movieproject.dto.imports.ImportResult;
import ru.vlsu.ispi.movieproject.dto.movie.MovieImportDto;
import ru.vlsu.ispi.movieproject.dto.imports.MovieListResponseDto;
import ru.vlsu.ispi.movieproject.mapper.MovieMapper;
import ru.vlsu.ispi.movieproject.model.Movie;
import ru.vlsu.ispi.movieproject.repository.MovieRepository;
import ru.vlsu.ispi.movieproject.service.KinopoiskApiService;
import ru.vlsu.ispi.movieproject.service.MovieImportService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MovieImportServiceImpl implements MovieImportService {
    private final MovieRepository movieRepository;
    private final KinopoiskApiService kinopoiskApiService;
    private final MovieMapper movieMapper;

    @Transactional
    @Override
    public ImportResult importMovies() {
        int imported = 0;
        int skipped = 0;

        Set<Integer> existingIds = movieRepository.findAllKinopoiskId();
        List<Movie> moviesToSave = new ArrayList<>();

        int page = 1;
        int totalPages;

        do{
            MovieListResponseDto response = kinopoiskApiService.getMovieList(page);
            totalPages = response.getTotalPages();

            for (MovieImportDto movie : response.getItems()){
                if (existingIds.contains(movie.getKinopoiskId())) {
                    skipped++;
                    continue;
                }
                Movie newMovie = movieMapper.fromMovieImportDto(movie);

                moviesToSave.add(newMovie);
                existingIds.add(movie.getKinopoiskId());
                imported++;
            }
            page++;
        }while(page <= totalPages);

        movieRepository.saveAll(moviesToSave);
        return new ImportResult(imported, skipped);
    }

}
