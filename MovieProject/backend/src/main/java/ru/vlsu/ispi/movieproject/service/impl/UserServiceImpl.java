package ru.vlsu.ispi.movieproject.service.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.vlsu.ispi.movieproject.dto.user.UserDto;
import ru.vlsu.ispi.movieproject.enums.FileDirectory;
import ru.vlsu.ispi.movieproject.exception.UserNotFoundException;
import ru.vlsu.ispi.movieproject.mapper.UserMapper;
import ru.vlsu.ispi.movieproject.model.User;
import ru.vlsu.ispi.movieproject.repository.UserRepository;
import ru.vlsu.ispi.movieproject.service.FileStorageService;
import ru.vlsu.ispi.movieproject.service.UserService;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(UserMapper::mapToDto).toList();
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Некорректный id пользователя"));

        return UserMapper.mapToDto(user);
    }

    @Override
    public UserDto updateAvatar(Long id, MultipartFile file) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Некорректный id пользователя"));

        fileStorageService.delete(user.getAvatarUrl());
        String avatarUrl = fileStorageService.upload(file, FileDirectory.AVATARS.getFolder());
        user.setAvatarUrl(avatarUrl);

        return UserMapper.mapToDto(user);
    }

    @Override
    @Transactional
    public UserDto updateProfile(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (userDto.getUsername() != null && !userDto.getUsername().isBlank()) {
            user.setUsername(userDto.getUsername());
        }

        user.setAboutMe(userDto.getAboutMe());

        User savedUser = userRepository.save(user);

        return new UserDto(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getRole().name(),
                savedUser.getAvatarUrl(),
                savedUser.getAboutMe()
        );
    }
}
