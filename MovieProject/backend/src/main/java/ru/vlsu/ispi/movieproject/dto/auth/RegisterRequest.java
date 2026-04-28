package ru.vlsu.ispi.movieproject.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.vlsu.ispi.movieproject.validation.NotReservedUsername;

@Data
public class RegisterRequest {
    @NotBlank
    @Size(min = 3, max = 50)
    @NotReservedUsername
    private String username;

    @NotBlank
    @Email
    @Size(min = 5, max = 50)
    private String email;

    @NotBlank
    @Size(min = 8)
    private String password;
}
