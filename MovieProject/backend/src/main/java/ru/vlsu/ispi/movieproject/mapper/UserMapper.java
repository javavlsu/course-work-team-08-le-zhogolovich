package ru.vlsu.ispi.movieproject.mapper;

import org.springframework.stereotype.Component;
import ru.vlsu.ispi.movieproject.dto.user.UserDto;
import ru.vlsu.ispi.movieproject.model.User;

@Component
public class UserMapper {


//    toUserDto стоит просто переименовать на mapToDto и просто заменить в первом методе в сервисе
    public static UserDto toUserDto(User user) {
        return new UserDto(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getRole().toString(),
            user.getAvatarUrl()
        );
    }
    public static UserDto mapToDto(User user) {
        UserDto dto = new UserDto();
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setRole(user.getRole().name());
        return dto;
    }
}
