package ru.vlsu.ispi.movieproject.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vlsu.ispi.movieproject.dto.country.CountryDto;
import ru.vlsu.ispi.movieproject.mapper.CountryMapper;
import ru.vlsu.ispi.movieproject.repository.CountryRepository;
import ru.vlsu.ispi.movieproject.service.CountryService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CountryServiceImpl implements CountryService {
    private final CountryRepository countryRepository;
    private final CountryMapper countryMapper;

    @Override
    public List<CountryDto> getAll() {
        return countryRepository.findAll().stream().map(countryMapper::toDto).toList();
    }
}
