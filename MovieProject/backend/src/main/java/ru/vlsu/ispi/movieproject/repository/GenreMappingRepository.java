package ru.vlsu.ispi.movieproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vlsu.ispi.movieproject.model.GenreMapping;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface GenreMappingRepository extends JpaRepository<GenreMapping, Long> {
    Optional<GenreMapping> findByExternalName(String externalName);

    List<GenreMapping> findAllByExternalNameIn(Collection<String> names);
}