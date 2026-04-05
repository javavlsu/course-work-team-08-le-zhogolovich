package ru.vlsu.ispi.movieproject.service;


import org.springframework.web.multipart.MultipartFile;
import ru.vlsu.ispi.movieproject.dto.user.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto getUserById(Long id);

    UserDto updateAvatar(Long id, MultipartFile file);
    UserDto updateProfile(Long id, UserDto userDto);}
