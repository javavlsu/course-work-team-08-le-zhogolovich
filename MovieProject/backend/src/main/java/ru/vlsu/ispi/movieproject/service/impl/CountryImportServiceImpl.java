package ru.vlsu.ispi.movieproject.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vlsu.ispi.movieproject.dto.imports.ImportResult;
import ru.vlsu.ispi.movieproject.dto.movie.CountryDto;
import ru.vlsu.ispi.movieproject.model.Country;
import ru.vlsu.ispi.movieproject.model.CountryMapping;
import ru.vlsu.ispi.movieproject.repository.CountryMappingRepository;
import ru.vlsu.ispi.movieproject.repository.CountryRepository;
import ru.vlsu.ispi.movieproject.service.CountryImportService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CountryImportServiceImpl implements CountryImportService {
    private final CountryRepository countryRepository;
    private final CountryMappingRepository countryMappingRepository;

    @Override
    public ImportResult importCountries(List<CountryDto> countries) {
        int imported = 0;
        int skipped = 0;

        Set<String> existingCountries = countryRepository.findAll()
                .stream()
                .map(g -> g.getName().trim().toLowerCase())
                .collect(Collectors.toSet());

        Set<String> existingMappings = countryMappingRepository.findAll()
                .stream()
                .map(m -> m.getExternalName().trim().toLowerCase())
                .collect(Collectors.toSet());

        List<Country> countriesToSave = new ArrayList<>();
        List<CountryMapping> mappingsToSave = new ArrayList<>();

        for (CountryDto dto : countries) {
            String name = dto.getCountry().trim().toLowerCase();
            Country country = null;
            if (!existingCountries.contains(name)) {
                country = new Country();
                country.setName(dto.getCountry());
                countriesToSave.add(country);
                existingCountries.add(name);
                imported++;
            } else skipped++;

            if (country != null && !existingMappings.contains(name)) {
                CountryMapping countryMapping = new CountryMapping();
                countryMapping.setExternalName(name);
                countryMapping.setCountry(country);
                mappingsToSave.add(countryMapping);
                existingMappings.add(name);
            }
        }

        countryRepository.saveAll(countriesToSave);
        countryMappingRepository.saveAll(mappingsToSave);

        return new ImportResult(imported, skipped, 0);
    }
}
