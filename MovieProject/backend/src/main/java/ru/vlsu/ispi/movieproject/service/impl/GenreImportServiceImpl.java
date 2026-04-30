package ru.vlsu.ispi.movieproject.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vlsu.ispi.movieproject.dto.imports.ImportResult;
import ru.vlsu.ispi.movieproject.dto.movie.GenreDto;
import ru.vlsu.ispi.movieproject.model.Genre;
import ru.vlsu.ispi.movieproject.model.GenreMapping;
import ru.vlsu.ispi.movieproject.repository.GenreMappingRepository;
import ru.vlsu.ispi.movieproject.repository.GenreRepository;
import ru.vlsu.ispi.movieproject.service.GenreImportService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenreImportServiceImpl implements GenreImportService {
    private final GenreRepository genreRepository;
    private final GenreMappingRepository genreMappingRepository;

    @Override
    public ImportResult importGenres(List<GenreDto> genres) {
        int imported = 0;
        int skipped = 0;

        Set<String> existingGenres = genreRepository.findAll()
                .stream()
                .map(g -> g.getName().trim().toLowerCase())
                .collect(Collectors.toSet());

        Set<String> existingMappings = genreMappingRepository.findAll()
                .stream()
                .map(m -> m.getExternalName().trim().toLowerCase())
                .collect(Collectors.toSet());

        List<Genre> genresToSave = new ArrayList<>();
        List<GenreMapping> mappingsToSave = new ArrayList<>();

        for (GenreDto dto : genres) {
            String name = dto.getGenre().trim().toLowerCase();
            Genre genre = null;
            if (!existingGenres.contains(name)) {
                genre = new Genre();
                genre.setName(dto.getGenre());
                genresToSave.add(genre);
                existingGenres.add(name);
                imported++;
            } else skipped++;

            if (genre != null && !existingMappings.contains(name)) {
                GenreMapping genreMapping = new GenreMapping();
                genreMapping.setExternalName(name);
                genreMapping.setGenre(genre);
                mappingsToSave.add(genreMapping);
                existingMappings.add(name);
            }
        }

        genreRepository.saveAll(genresToSave);
        genreMappingRepository.saveAll(mappingsToSave);

        return new ImportResult(imported, skipped, 0);
    }
}
