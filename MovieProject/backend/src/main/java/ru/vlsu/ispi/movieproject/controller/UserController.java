package ru.vlsu.ispi.movieproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.vlsu.ispi.movieproject.dto.user.EditProfileRequest;
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
    public UserDto getCurrentUser() {
        return userService.getCurrentUser();
    }

    @GetMapping("/{username}")
    public UserDto getUser(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }

    @PatchMapping("/me")
    public UserDto updateProfile(@RequestBody @Valid EditProfileRequest request) {
        return userService.updateProfile(request);
    }

    @PatchMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UserDto uploadAvatar(@RequestParam("file") MultipartFile file) {
        return userService.updateAvatar(file);
    }
}
