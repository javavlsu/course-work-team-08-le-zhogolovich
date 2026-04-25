package ru.vlsu.ispi.movieproject.dto.compilation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CreateCompilationRequest {
    @NotBlank
    @Size(min = 1, max = 255)
    private String title;

    @Size(max = 2000)
    private String description;

    @NotNull
    private Boolean isPublic;
}
