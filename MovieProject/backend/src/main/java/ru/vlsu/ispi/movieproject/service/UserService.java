package ru.vlsu.ispi.movieproject.service;


import ru.vlsu.ispi.movieproject.dto.user.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();
}
