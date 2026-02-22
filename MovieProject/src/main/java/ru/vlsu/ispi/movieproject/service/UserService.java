package ru.vlsu.ispi.movieproject.service;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.vlsu.ispi.movieproject.dto.LoginRequest;
import ru.vlsu.ispi.movieproject.dto.RegisterRequest;
import ru.vlsu.ispi.movieproject.model.User;
import ru.vlsu.ispi.movieproject.repository.UserRepository;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Пользователь с таким email уже существует.");
        }
        User user = new User();
        user.setEmail(request.getEmail());

        String hashedPassword = passwordEncoder.encode(request.getPassword());
        user.setPasswordHash(hashedPassword);

        userRepository.save(user);
    }

    public boolean login(LoginRequest request) {
        User user = userRepository.findbyEmail(request.getEmail()).
                orElseThrow(() -> new RuntimeException("Пользователь не найден."));

        return passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash()
        );
    }
}
