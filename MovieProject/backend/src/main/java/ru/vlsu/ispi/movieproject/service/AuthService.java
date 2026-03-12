package ru.vlsu.ispi.movieproject.service;

import ru.vlsu.ispi.movieproject.dto.LoginRequest;
import ru.vlsu.ispi.movieproject.dto.RefreshTokenDto;
import ru.vlsu.ispi.movieproject.dto.RegisterRequest;
import ru.vlsu.ispi.movieproject.dto.JwtAuthenticationDto;


public interface AuthService {
    void register(RegisterRequest registerRequest);
    JwtAuthenticationDto login(LoginRequest loginRequest);
    JwtAuthenticationDto refreshToken(RefreshTokenDto refreshTokenDto);
}
