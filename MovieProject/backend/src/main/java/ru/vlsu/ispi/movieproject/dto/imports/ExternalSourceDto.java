package ru.vlsu.ispi.movieproject.dto.imports;

import lombok.Data;

@Data
public class ExternalSourceDto {
    private String url;
    private String platform;
    private String logoUrl;
}
