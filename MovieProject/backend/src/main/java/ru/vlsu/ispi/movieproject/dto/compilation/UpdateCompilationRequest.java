package ru.vlsu.ispi.movieproject.dto.compilation;

import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UpdateCompilationRequest {
    @Size(min = 8, max = 255)
    private String title;

    @Size(max = 2000)
    private String description;

    private Boolean isPublic;
}

