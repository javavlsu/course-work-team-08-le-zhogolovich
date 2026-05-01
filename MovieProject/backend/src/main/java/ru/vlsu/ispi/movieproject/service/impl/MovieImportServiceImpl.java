package ru.vlsu.ispi.movieproject.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.vlsu.ispi.movieproject.dto.imports.ImportResult;
import ru.vlsu.ispi.movieproject.dto.imports.MovieImportErrorDto;
import ru.vlsu.ispi.movieproject.dto.imports.MovieImportResultDto;
import ru.vlsu.ispi.movieproject.dto.imports.MovieListResponseDto;
import ru.vlsu.ispi.movieproject.dto.movie.MovieImportDto;
import ru.vlsu.ispi.movieproject.mapper.MovieMapper;
import ru.vlsu.ispi.movieproject.model.Movie;
import ru.vlsu.ispi.movieproject.repository.MovieRepository;
import ru.vlsu.ispi.movieproject.service.FileStorageService;
import ru.vlsu.ispi.movieproject.service.ImportService;
import ru.vlsu.ispi.movieproject.service.KinopoiskApiService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieImportServiceImpl implements ImportService<MovieImportResultDto> {
    private final MovieRepository movieRepository;
    private final KinopoiskApiService kinopoiskApiService;
    private final MovieMapper movieMapper;
    private final FileStorageService fileStorageService;

    @Override
    public MovieImportResultDto importData() {
        int imported = 0;
        int skipped = 0;
        int failed = 0;

        Set<Integer> existingIds = movieRepository.findAllKinopoiskId();
        List<Movie> moviesToSave = new ArrayList<>();
        MovieImportResultDto result = new MovieImportResultDto();

        int page = 1;
        int totalPages;

        do {
            MovieListResponseDto response = kinopoiskApiService.getMovieList(page);
            totalPages = response.getTotalPages();

            for (MovieImportDto movie : response.getItems()) {
                try {
                    if (existingIds.contains(movie.getKinopoiskId())) {
                        skipped++;
                        continue;
                    }

                    Movie newMovie = movieMapper.fromMovieImportDto(movie);

                    String localPath = fileStorageService.downloadPosterAndSave(movie.getPosterUrl());
                    newMovie.setPosterUrl(localPath);

                    moviesToSave.add(newMovie);
                    existingIds.add(movie.getKinopoiskId());
                    imported++;

                } catch (Exception e) {
                    failed++;

                    String name = movie.getNameOriginal() != null ? movie.getNameOriginal()
                            : movie.getNameRu() != null ? movie.getNameRu() : "UNKNOWN";

                    result.getErrors().add(
                            new MovieImportErrorDto(name, e.getMessage(), movie.getPosterUrl()
                            )
                    );
                }
            }

            page++;
        } while (page <= totalPages);

        try {
            movieRepository.saveAll(moviesToSave);
        } catch (Exception e) {
            log.error("Batch save failed", e);
        }

        result.setResults(new ImportResult(imported, skipped, failed));
        return result;
    }
}
