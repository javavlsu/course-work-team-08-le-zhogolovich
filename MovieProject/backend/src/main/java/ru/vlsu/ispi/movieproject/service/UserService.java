package ru.vlsu.ispi.movieproject.service;

import org.springframework.web.multipart.MultipartFile;
import ru.vlsu.ispi.movieproject.dto.user.EditProfileRequest;
import ru.vlsu.ispi.movieproject.dto.user.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();
    UserDto getCurrentUser();
    UserDto getUserById(Long id);
    UserDto getUserByUsername(String username);
    UserDto updateAvatar(MultipartFile file);
    UserDto updateProfile(EditProfileRequest request);
}
