package ru.vlsu.ispi.movieproject.dto.user;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EditProfileRequest {
    @Size(min = 3, max = 50)
    private String username;

    @Size(min = 5, max = 50)
    private String email;

    @Size(max = 1000)
    private String aboutMe;
}
