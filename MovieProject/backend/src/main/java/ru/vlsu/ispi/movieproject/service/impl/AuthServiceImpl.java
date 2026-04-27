package ru.vlsu.ispi.movieproject.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.vlsu.ispi.movieproject.dto.auth.JwtAuthenticationDto;
import ru.vlsu.ispi.movieproject.dto.auth.LoginRequest;
import ru.vlsu.ispi.movieproject.dto.auth.RefreshTokenDto;
import ru.vlsu.ispi.movieproject.dto.auth.RegisterRequest;
import ru.vlsu.ispi.movieproject.enums.Role;
import ru.vlsu.ispi.movieproject.exception.InvalidTokenException;
import ru.vlsu.ispi.movieproject.exception.UserAlreadyExistsException;
import ru.vlsu.ispi.movieproject.model.User;
import ru.vlsu.ispi.movieproject.repository.UserRepository;
import ru.vlsu.ispi.movieproject.security.CustomUserDetails;
import ru.vlsu.ispi.movieproject.security.jwt.JwtService;
import ru.vlsu.ispi.movieproject.service.AuthService;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public void register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmailAndDeletedFalse(registerRequest.getEmail())) {
            throw new UserAlreadyExistsException();
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(Role.USER);

        userRepository.save(user);
    }

    @Override
    public JwtAuthenticationDto login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getLogin(),
                        loginRequest.getPassword()
                )
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return jwtService.generateAuthToken(
                userDetails.getId(),
                userDetails.getRole()
        );
    }

    @Override
    public JwtAuthenticationDto refreshToken(RefreshTokenDto refreshTokenDto) {
        String refreshToken = refreshTokenDto.getRefreshToken();

        if (!jwtService.isTokenValid(refreshToken)) {
            throw new InvalidTokenException();
        }

        Long userId = jwtService.extractUserId(refreshToken);

        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Некорректный id пользователя"));

        return jwtService.generateAuthToken(
                user.getId(),
                user.getRole().toString()
        );
    }
}
