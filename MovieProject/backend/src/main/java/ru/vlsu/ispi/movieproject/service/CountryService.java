package ru.vlsu.ispi.movieproject.service;

import ru.vlsu.ispi.movieproject.dto.country.CountryDto;

import java.util.List;

public interface CountryService {
    List<CountryDto> getAll();
}
