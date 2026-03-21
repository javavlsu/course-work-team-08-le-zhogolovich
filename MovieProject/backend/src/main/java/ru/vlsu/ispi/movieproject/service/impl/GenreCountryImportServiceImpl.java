package ru.vlsu.ispi.movieproject.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vlsu.ispi.movieproject.dto.imports.FiltersResponseDto;
import ru.vlsu.ispi.movieproject.dto.movie.CountryDto;
import ru.vlsu.ispi.movieproject.dto.movie.GenreDto;
import ru.vlsu.ispi.movieproject.model.Country;
import ru.vlsu.ispi.movieproject.model.CountryMapping;
import ru.vlsu.ispi.movieproject.model.Genre;
import ru.vlsu.ispi.movieproject.model.GenreMapping;
import ru.vlsu.ispi.movieproject.repository.CountryMappingRepository;
import ru.vlsu.ispi.movieproject.repository.CountryRepository;
import ru.vlsu.ispi.movieproject.repository.GenreMappingRepository;
import ru.vlsu.ispi.movieproject.repository.GenreRepository;
import ru.vlsu.ispi.movieproject.service.GenreCountryImportService;
import ru.vlsu.ispi.movieproject.service.KinopoiskApiService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenreCountryImportServiceImpl implements GenreCountryImportService {
    private final GenreRepository genreRepository;
    private final CountryRepository countryRepository;
    private final KinopoiskApiService kinopoiskApiService;
    private final GenreMappingRepository genreMappingRepository;
    private final CountryMappingRepository countryMappingRepository;

    @Override
    @Transactional
    public void importGenreCountry() {
        FiltersResponseDto filters = kinopoiskApiService.getFilters();

        Set<String> existingGenres = genreRepository.findAll()
                .stream()
                .map(g -> g.getName().trim().toLowerCase())
                .collect(Collectors.toSet());

        Set<String> existingCountries = countryRepository.findAll()
                .stream()
                .map(g -> g.getName().trim().toLowerCase())
                .collect(Collectors.toSet());

        Set<String> existingGenreMappings = genreMappingRepository.findAll()
                .stream()
                .map(m -> m.getExternalName().trim().toLowerCase())
                .collect(Collectors.toSet());

        Set<String> existingCountryMappings = countryMappingRepository.findAll()
                .stream()
                .map(m -> m.getExternalName().trim().toLowerCase())
                .collect(Collectors.toSet());

        List<Genre> genresToSave = new ArrayList<>();
        List<GenreMapping> genreMappingsToSave = new ArrayList<>();

        List<Country> countriesToSave = new ArrayList<>();
        List<CountryMapping> countryMappingsToSave = new ArrayList<>();

        for (GenreDto dto : filters.getGenres()) {
            String name = dto.getGenre().trim().toLowerCase();
            Genre genre = null;
            if (!existingGenres.contains(name)) {
                genre = new Genre();
                genre.setName(dto.getGenre());
                genresToSave.add(genre);
                existingGenres.add(name);
            }

            if (genre != null && !existingGenreMappings.contains(name)) {
                GenreMapping genreMapping = new GenreMapping();
                genreMapping.setExternalName(name);
                genreMapping.setGenre(genre);
                genreMappingsToSave.add(genreMapping);
                existingGenreMappings.add(name);
            }
        }

        for (CountryDto dto : filters.getCountries()) {
            String name = dto.getCountry().trim().toLowerCase();
            Country country = null;
            if (!existingCountries.contains(name)) {
                country = new Country();
                country.setName(dto.getCountry());
                countriesToSave.add(country);
                existingCountries.add(name);
            }

            if (country != null && !existingCountryMappings.contains(name)) {
                CountryMapping countryMapping = new CountryMapping();
                countryMapping.setExternalName(name);
                countryMapping.setCountry(country);
                countryMappingsToSave.add(countryMapping);
                existingCountryMappings.add(name);
            }
        }

        genreRepository.saveAll(genresToSave);
        countryRepository.saveAll(countriesToSave);
        genreMappingRepository.saveAll(genreMappingsToSave);
        countryMappingRepository.saveAll(countryMappingsToSave);
    }
}
