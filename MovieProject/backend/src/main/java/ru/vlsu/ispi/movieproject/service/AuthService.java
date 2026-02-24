package ru.vlsu.ispi.movieproject.service;

import ru.vlsu.ispi.movieproject.dto.*;

public interface AuthService {
    void register(RegisterRequest registerRequest);
    JwtAuthenticationDto login(LoginRequest loginRequest);
    JwtAuthenticationDto refreshToken(RefreshTokenDto refreshTokenDto);
}
