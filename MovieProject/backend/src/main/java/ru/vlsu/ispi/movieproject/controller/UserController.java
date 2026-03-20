package ru.vlsu.ispi.movieproject.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.vlsu.ispi.movieproject.dto.user.UserDto;
import ru.vlsu.ispi.movieproject.service.UserService;
import org.springframework.security.core.Authentication;
import ru.vlsu.ispi.movieproject.model.User;
import ru.vlsu.ispi.movieproject.repository.UserRepository;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;

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
