package ru.vlsu.ispi.movieproject.mapper;

import org.springframework.stereotype.Component;
import ru.vlsu.ispi.movieproject.dto.country.CountryDto;
import ru.vlsu.ispi.movieproject.model.Country;

@Component
public class CountryMapper {
    public CountryDto toDto(Country country) {
        return new CountryDto(
                country.getId(),
                country.getName()
        );
    }
}
