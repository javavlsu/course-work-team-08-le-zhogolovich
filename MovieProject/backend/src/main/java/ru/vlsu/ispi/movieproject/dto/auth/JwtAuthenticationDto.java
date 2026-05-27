package ru.vlsu.ispi.movieproject.dto.auth;

import lombok.Data;

@Data
public class JwtAuthenticationDto {
    private String token;
    private String refreshToken;
}
