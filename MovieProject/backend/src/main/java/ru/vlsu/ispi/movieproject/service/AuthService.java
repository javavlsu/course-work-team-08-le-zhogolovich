package ru.vlsu.ispi.movieproject.service;

import ru.vlsu.ispi.movieproject.dto.auth.LoginRequest;
import ru.vlsu.ispi.movieproject.dto.auth.RegisterRequest;
import ru.vlsu.ispi.movieproject.dto.auth.RefreshTokenDto;
import ru.vlsu.ispi.movieproject.dto.auth.JwtAuthenticationDto;


public interface AuthService {
    void register(RegisterRequest registerRequest);
    JwtAuthenticationDto login(LoginRequest loginRequest);
    JwtAuthenticationDto refreshToken(RefreshTokenDto refreshTokenDto);
}
