package ru.vlsu.ispi.movieproject.dto.compilation;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UpdateCompilationRequest {
    private String title;
    private String description;
    private Boolean isPublic;
    private MultipartFile cover;
}

