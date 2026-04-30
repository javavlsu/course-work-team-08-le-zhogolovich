package ru.vlsu.ispi.movieproject.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import ru.vlsu.ispi.movieproject.dto.imports.EnrichmentErrorDto;
import ru.vlsu.ispi.movieproject.dto.imports.EnrichmentResultDto;
import ru.vlsu.ispi.movieproject.repository.MovieRepository;
import ru.vlsu.ispi.movieproject.service.EnrichMovieService;
import ru.vlsu.ispi.movieproject.service.EnrichService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrichServiceImpl implements EnrichService {
    private final EnrichMovieService enrichMovieService;
    private final MovieRepository movieRepository;

    @Override
    public EnrichmentResultDto enrichAll() throws InterruptedException {
        int enriched = 0;
        int skipped = 0;
        int failed = 0;

        EnrichmentResultDto result = new EnrichmentResultDto();

        List<Long> movieIds = movieRepository.findAllIds();
        LocalDateTime now = LocalDateTime.now();

        for (Long id : movieIds) {
            int attempts = 0;
            while (attempts < 3) {
                try {
                    boolean updated = enrichMovieService.enrichMovie(id, now);
                    if (updated) enriched++;
                    else skipped++;

                    Thread.sleep(120);
                    break;
                } catch (HttpClientErrorException.TooManyRequests e) {
                    attempts++;
                    Thread.sleep(1000);
                } catch (Exception e) {
                    failed++;
                    result.getErrors().add(new EnrichmentErrorDto(id, e.getMessage()));
                    break;
                }
            }
            if (attempts == 3) {
                failed++;

                result.getErrors().add(
                        new EnrichmentErrorDto(id, "Too many requests (max retries reached)"));
            }
        }

        result.setEnriched(enriched);
        result.setSkipped(skipped);
        result.setFailed(failed);

        return result;
    }
}
