package ru.vlsu.ispi.movieproject.service;

import ru.vlsu.ispi.movieproject.dto.auth.AuthRequest;
import ru.vlsu.ispi.movieproject.dto.auth.RefreshTokenDto;
import ru.vlsu.ispi.movieproject.dto.auth.JwtAuthenticationDto;


public interface AuthService {
    void register(AuthRequest authRequest);
    JwtAuthenticationDto login(AuthRequest authRequest);
    JwtAuthenticationDto refreshToken(RefreshTokenDto refreshTokenDto);
}
