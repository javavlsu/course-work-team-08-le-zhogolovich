package ru.vlsu.ispi.movieproject.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.vlsu.ispi.movieproject.dto.user.UserDto;
import ru.vlsu.ispi.movieproject.security.CustomUserDetails;
import ru.vlsu.ispi.movieproject.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@PreAuthorize("isAuthenticated()")
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping()
    public List<UserDto> getUsers() {
        return userService.getAllUsers();
        }

    @GetMapping("/me")
    public UserDto getCurrentUser(@AuthenticationPrincipal CustomUserDetails user) {
        return userService.getUserById(user.getId());
    }
    @PatchMapping("/me")
    public UserDto updateProfile(@AuthenticationPrincipal CustomUserDetails userDetails,
                                 @RequestBody UserDto userDto) {
        // Вызываем сервис, передавая ID из токена и данные из тела запроса
        return userService.updateProfile(userDetails.getId(), userDto);
    }

    @PatchMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UserDto uploadAvatar(@AuthenticationPrincipal CustomUserDetails user,
                                @RequestParam("file") MultipartFile file) {
        System.out.println("FILE EMPTY: " + file.isEmpty());
        System.out.println("FILE NAME: " + file.getOriginalFilename());
        return userService.updateAvatar(user.getId(), file);
    }
}
