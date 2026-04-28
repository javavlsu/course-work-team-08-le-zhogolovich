package ru.vlsu.ispi.movieproject.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.vlsu.ispi.movieproject.dto.imports.ExternalSourceDto;
import ru.vlsu.ispi.movieproject.dto.movie.MovieDto;
import ru.vlsu.ispi.movieproject.dto.movie.MovieFullDto;
import ru.vlsu.ispi.movieproject.dto.movie.MovieImportDto;
import ru.vlsu.ispi.movieproject.exception.CountryMapException;
import ru.vlsu.ispi.movieproject.exception.GenreMapException;
import ru.vlsu.ispi.movieproject.model.Country;
import ru.vlsu.ispi.movieproject.model.CountryMapping;
import ru.vlsu.ispi.movieproject.model.Genre;
import ru.vlsu.ispi.movieproject.model.GenreMapping;
import ru.vlsu.ispi.movieproject.model.Movie;
import ru.vlsu.ispi.movieproject.model.Tag;
import ru.vlsu.ispi.movieproject.repository.CountryMappingRepository;
import ru.vlsu.ispi.movieproject.repository.GenreMappingRepository;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MovieMapper {
    private final GenreMappingRepository genreMappingRepository;
    private final CountryMappingRepository countryMappingRepository;

    public Movie fromMovieImportDto(MovieImportDto dto) {
        Movie movie = new Movie();
        movie.setKinopoiskId(dto.getKinopoiskId());
        movie.setName(dto.getNameRu());
        movie.setOriginalName(dto.getNameOriginal());
        movie.setReleaseYear(dto.getYear());
        movie.setPosterUrl(dto.getPosterUrl());
        movie.setRatingKinopoisk(dto.getRatingKinopoisk());
        movie.setRatingImdb(dto.getRatingImdb());

        if (dto.getGenres() != null) {
            var names = dto.getGenres().stream()
                    .map(g -> normalize(g.getGenre()))
                    .toList();

            var mappings = genreMappingRepository.findAllByExternalNameIn(names);

            var map = mappings.stream()
                    .collect(Collectors.toMap(
                            m -> normalize(m.getExternalName()),
                            GenreMapping::getGenre
                    ));

            movie.getGenres().addAll(
                    names.stream()
                            .map(name -> {
                                Genre genre = map.get(name);
                                if (genre == null) { throw new GenreMapException(name); }
                                return genre;
                            }).collect(Collectors.toSet())
            );
        }

        if (dto.getCountries() != null) {
            var names = dto.getCountries().stream()
                    .map(c -> normalize(c.getCountry()))
                    .toList();

            var mappings = countryMappingRepository.findAllByExternalNameIn(names);

            var map = mappings.stream()
                    .collect(Collectors.toMap(
                            m -> normalize(m.getExternalName()),
                            CountryMapping::getCountry
                    ));

            movie.getCountries().addAll(
                    names.stream()
                            .map(name -> {
                                Country country = map.get(name);
                                if (country == null) { throw new CountryMapException(name); }
                                return country;
                            }).collect(Collectors.toSet())
            );
        }

        return movie;
    }

    private String normalize(String value) {
        return value.trim().toLowerCase();
    }

    public MovieDto toMovieDto(Movie movie) {
        return new MovieDto(
                movie.getId(),
                movie.getKinopoiskId(),
                movie.getName(),
                movie.getPosterUrl(),
                movie.getReleaseYear(),
                movie.getAvgRating(),
                movie.getRatingKinopoisk(),
                movie.getRatingImdb()
        );
    }

    public MovieFullDto toFullDto(Movie movie) {
        MovieFullDto dto = new MovieFullDto();

        dto.setId(movie.getId());
        dto.setKinopoiskId(movie.getKinopoiskId());
        dto.setName(movie.getName());
        dto.setNameOriginal(movie.getOriginalName());
        dto.setReleaseYear(movie.getReleaseYear());
        dto.setPosterUrl(movie.getPosterUrl());
        dto.setOverview(movie.getOverview());
        dto.setAvgRating(movie.getAvgRating());
        dto.setRatingKinopoisk(movie.getRatingKinopoisk());
        dto.setRatingImdb(movie.getRatingImdb());
        dto.setRatingsCount(movie.getRatingsCount());

        dto.setGenres(
                movie.getGenres().stream()
                        .map(Genre::getName)
                        .collect(Collectors.toSet())
        );

        dto.setCountries(
                movie.getCountries().stream()
                        .map(Country::getName)
                        .collect(Collectors.toSet())
        );

        dto.setTags(
                movie.getTags().stream()
                        .map(Tag::getName)
                        .collect(Collectors.toSet())
        );

        dto.setExternalSources(
                movie.getExternalSources().stream()
                        .map(source -> {
                            ExternalSourceDto s = new ExternalSourceDto();
                            s.setUrl(source.getUrl());
                            s.setPlatform(source.getPlatform());
                            s.setLogoUrl(source.getLogoUrl());
                            return s;
                        })
                        .collect(Collectors.toSet())
        );

        return dto;
    }
}
