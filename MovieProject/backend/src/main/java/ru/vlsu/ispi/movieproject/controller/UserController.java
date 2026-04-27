package ru.vlsu.ispi.movieproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.vlsu.ispi.movieproject.dto.user.EditProfileRequest;
import ru.vlsu.ispi.movieproject.dto.user.UserDto;
import ru.vlsu.ispi.movieproject.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping()
    public List<UserDto> getUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/me")
    public UserDto getCurrentUser() {
        return userService.getCurrentUser();
    }

    @GetMapping("/id/{id}")
    public UserDto getUser(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/{username}")
    public UserDto getUserByUsername(@PathVariable String username) {
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

    @PostMapping("/follow/{id}")
    public void followUser(@PathVariable Long id) {
        userService.follow(id);
    }

    @DeleteMapping("/follow/{id}")
    public void unfollowUser(@PathVariable Long id) {
        userService.unfollow(id);
    }

    @GetMapping("/me/followers")
    public List<UserDto> getFollowers() {
        return userService.getFollowers();
    }

    @GetMapping("/me/followings")
    public List<UserDto> getFollowings() {
        return userService.getFollowings();
    }

    @GetMapping("/{username}/followers")
    public List<UserDto> getFollowersByUsername(@PathVariable String username) {
        return userService.getFollowersByUsername(username);
    }

    @GetMapping("/{username}/followings")
    public List<UserDto> getFollowingsByUsername(@PathVariable String username) {
        return userService.getFollowingsByUsername(username);
    }

    @DeleteMapping("/me/delete-profile")
    public void deleteProfile() {
        userService.deleteProfile();
    }

    @DeleteMapping("/id/{id}/delete-profile")
    public void deleteProfileById(@PathVariable Long id) {
        userService.deleteUserById(id);
    }
}
