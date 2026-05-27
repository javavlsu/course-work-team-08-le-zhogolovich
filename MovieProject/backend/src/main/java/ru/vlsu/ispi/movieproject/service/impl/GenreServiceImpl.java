package ru.vlsu.ispi.movieproject.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vlsu.ispi.movieproject.dto.genre.GenreDto;
import ru.vlsu.ispi.movieproject.mapper.GenreMapper;
import ru.vlsu.ispi.movieproject.repository.GenreRepository;
import ru.vlsu.ispi.movieproject.service.GenreService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;

    @Override
    public List<GenreDto> getAll() {
        return genreRepository.findAll().stream().map(genreMapper::toDto).toList();
    }
}
