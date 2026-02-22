package ru.vlsu.ispi.movieproject.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.vlsu.ispi.movieproject.dto.LoginRequest;
import ru.vlsu.ispi.movieproject.dto.RegisterRequest;
import ru.vlsu.ispi.movieproject.service.UserService;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        userService.register(request);
        return ResponseEntity.ok("Пользователь успешно зарегистрирован");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        boolean logged = userService.login(request);
        if (logged) {
            return ResponseEntity.ok("Успешный вход.");
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неверный пароль или email");
        }
    }
}
