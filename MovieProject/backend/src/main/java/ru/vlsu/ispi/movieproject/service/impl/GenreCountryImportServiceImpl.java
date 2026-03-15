package ru.vlsu.ispi.movieproject.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vlsu.ispi.movieproject.dto.imports.FiltersResponseDto;
import ru.vlsu.ispi.movieproject.dto.movie.CountryDto;
import ru.vlsu.ispi.movieproject.dto.movie.GenreDto;
import ru.vlsu.ispi.movieproject.model.Country;
import ru.vlsu.ispi.movieproject.model.Genre;
import ru.vlsu.ispi.movieproject.repository.CountryRepository;
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

    @Override
    @Transactional
    public void importGenreCountry() {
        FiltersResponseDto filters = kinopoiskApiService.getFilters();

        Set<String> existingGenres = genreRepository.findAll()
                .stream()
                .map(Genre::getName)
                .collect(Collectors.toSet());

        Set<String> existingCountries = countryRepository.findAll()
                .stream()
                .map(Country::getName)
                .collect(Collectors.toSet());

        List<Genre> genresToSave = new ArrayList<>();
        List<Country> countriesToSave = new ArrayList<>();

        for (GenreDto dto : filters.getGenres()) {
            if (!existingGenres.contains(dto.getGenre())) {
                Genre genre = new Genre();
                genre.setName(dto.getGenre());
                genresToSave.add(genre);
            }
        }

        for (CountryDto dto : filters.getCountries()) {
            if (!existingCountries.contains(dto.getCountry())) {
                Country country = new Country();
                country.setName(dto.getCountry());
                countriesToSave.add(country);
            }
        }

        genreRepository.saveAll(genresToSave);
        countryRepository.saveAll(countriesToSave);
    }
}
