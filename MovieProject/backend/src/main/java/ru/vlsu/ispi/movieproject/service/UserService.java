package ru.vlsu.ispi.movieproject.service;


import org.springframework.web.multipart.MultipartFile;
import ru.vlsu.ispi.movieproject.dto.user.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto getUserByEmail(String name);

    UserDto updateAvatar(String email, MultipartFile file);
}
