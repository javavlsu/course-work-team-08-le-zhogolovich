package ru.vlsu.ispi.movieproject.service.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.vlsu.ispi.movieproject.dto.user.EditProfileRequest;
import ru.vlsu.ispi.movieproject.dto.user.UserDto;
import ru.vlsu.ispi.movieproject.enums.FileDirectory;
import ru.vlsu.ispi.movieproject.exception.UserNotFoundException;
import ru.vlsu.ispi.movieproject.mapper.UserMapper;
import ru.vlsu.ispi.movieproject.model.User;
import ru.vlsu.ispi.movieproject.repository.UserRepository;
import ru.vlsu.ispi.movieproject.service.CurrentUserService;
import ru.vlsu.ispi.movieproject.service.FileStorageService;
import ru.vlsu.ispi.movieproject.service.UserService;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final CurrentUserService currentUserService;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::mapToDto).toList();
    }

    @Override
    public UserDto getCurrentUser() {
        Long userId = currentUserService.getCurrentUserID();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException());

        return userMapper.mapToDto(user);
    }

    @Override
    public UserDto getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException());

        return userMapper.mapToDto(user);
    }

    @Override
    public UserDto updateAvatar(MultipartFile file) {
        Long userId = currentUserService.getCurrentUserID();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException());

        String avatarUrl = fileStorageService.upload(file, FileDirectory.AVATARS.getFolder());
        fileStorageService.delete(user.getAvatarUrl());
        user.setAvatarUrl(avatarUrl);

        return userMapper.mapToDto(user);
    }

    @Override
    public UserDto updateProfile(EditProfileRequest request) {
        Long userId = currentUserService.getCurrentUserID();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException());

        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            user.setUsername(request.getUsername());
        }

        user.setAboutMe(request.getAboutMe());

        return userMapper.mapToDto(user);
    }
}
