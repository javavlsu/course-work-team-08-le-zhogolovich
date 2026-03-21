package ru.vlsu.ispi.movieproject.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.vlsu.ispi.movieproject.dto.user.UserDto;
import ru.vlsu.ispi.movieproject.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {
    private final UserService userService;

    @GetMapping()
    public List<UserDto> getUsers() {
        return userService.getAllUsers();
        }

    @GetMapping("/me")
    public UserDto getCurrentUser(Authentication authentication) {
        return userService.getUserByEmail(authentication.getName());
    }

    @PatchMapping("/me/avatar")
    public UserDto uploadAvatar(Authentication authentication,
                                @RequestParam("file") MultipartFile file) {
        String email = authentication.getName();
        return userService.updateAvatar(email, file);
    }


}
