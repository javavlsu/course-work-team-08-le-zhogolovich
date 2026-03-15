package ru.vlsu.ispi.movieproject.mapper;

import org.springframework.stereotype.Component;
import ru.vlsu.ispi.movieproject.dto.movie.MovieDto;
import ru.vlsu.ispi.movieproject.exception.CountryMapException;
import ru.vlsu.ispi.movieproject.exception.GenreMapException;
import ru.vlsu.ispi.movieproject.model.Country;
import ru.vlsu.ispi.movieproject.model.Genre;
import ru.vlsu.ispi.movieproject.model.Movie;
import ru.vlsu.ispi.movieproject.repository.CountryRepository;
import ru.vlsu.ispi.movieproject.repository.GenreRepository;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class MovieMapper {
    private final Map<String, Genre> genresMap;
    private final Map<String, Country> countriesMap;

    public MovieMapper(GenreRepository genreRepository, CountryRepository countryRepository) {
        this.genresMap = genreRepository.findAll().stream()
                .collect(Collectors.toMap(
                        genre -> genre.getName().toLowerCase(),
                        genre -> genre
                ));
        this.countriesMap = countryRepository.findAll().stream()
                .collect(Collectors.toMap(
                        country -> country.getName().toLowerCase(),
                        country -> country
                ));
    }

    public Movie fromMovieDto(MovieDto dto) {
        Movie movie = new Movie();
        movie.setKinopoiskId(dto.getKinopoiskId());
        movie.setName(dto.getName());
        movie.setOriginalName(dto.getNameOriginal());
        movie.setReleaseYear(dto.getYear());
        movie.setPosterUrl(dto.getPosterUrl());
        if (dto.getGenres() != null) {
            movie.getGenres().addAll(dto.getGenres().stream()
                    .map(g -> mapGenre(g.getGenre()))
                    .collect(Collectors.toSet()));
        }

        if (dto.getCountries() != null) {
            movie.getCountries().addAll(dto.getCountries().stream()
                    .map(c -> mapCountry(c.getCountry()))
                    .collect(Collectors.toSet()));
        }

        return movie;
    }

    private Country mapCountry(String name) {
        Country country = countriesMap.get(name.toLowerCase());
        if (country == null) {
            throw new CountryMapException(name);
        }
        return country;
    }

    private Genre mapGenre(String name) {
        Genre genre = genresMap.get(name.toLowerCase());
        if (genre == null) {
            throw new GenreMapException(name);
        }
        return genre;
    }

    //    public static MovieDto toDto(Movie movie) {
//        return new MovieDto(
//                movie.getId(),
//                movie.getKinopoiskId(),
//                movie.getName(),
//                movie.getOriginalName(),
//                movie.getReleaseYear(),
//                movie.getPosterUrl(),
//                movie.getOverview(),
//                movie.getGenres().stream()
//                        .map(Genre::getName)
//                        .collect(Collectors.toSet()),
//                movie.getCountries().stream()
//                        .map(Country::getName)
//                        .collect(Collectors.toSet()),
//                movie.getTags().stream()
//                        .map(Tag::getName)
//                        .collect(Collectors.toSet())
//                );
//    }
}
