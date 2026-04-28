package ru.vlsu.ispi.movieproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vlsu.ispi.movieproject.model.CountryMapping;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CountryMappingRepository extends JpaRepository<CountryMapping, Long> {
    Optional<CountryMapping> findByExternalName(String externalName);

    List<CountryMapping> findAllByExternalNameIn(Collection<String> names);
}