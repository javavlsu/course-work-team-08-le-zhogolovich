package ru.vlsu.ispi.movieproject.service;

import ru.vlsu.ispi.movieproject.dto.imports.EnrichmentResultDto;

public interface EnrichService {
    EnrichmentResultDto enrichAll() throws InterruptedException;
}
