package ru.vlsu.ispi.movieproject.service;

import ru.vlsu.ispi.movieproject.dto.genre.GenreDto;

import java.util.List;

public interface GenreService {
    List<GenreDto> getAll();
}
