package ru.vlsu.ispi.movieproject.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.vlsu.ispi.movieproject.dto.user.UserDto;
import ru.vlsu.ispi.movieproject.exception.FilesException;
import ru.vlsu.ispi.movieproject.exception.UserNotFoundException;
import ru.vlsu.ispi.movieproject.mapper.UserMapper;
import ru.vlsu.ispi.movieproject.model.User;
import ru.vlsu.ispi.movieproject.repository.UserRepository;
import ru.vlsu.ispi.movieproject.service.UserService;

import java.io.IOException;
import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(UserMapper::toUserDto).toList();
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

        if (file == null || file.isEmpty()) {
            throw new FilesException("Файл пуст");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new FilesException("Файл должен быть изображением");
        }

        try {
            String uploadDir = "uploads/avatars/";
            java.nio.file.Path rootPath = java.nio.file.Paths.get(uploadDir);

            if (!java.nio.file.Files.exists(rootPath)) {
                java.nio.file.Files.createDirectories(rootPath);
            }

            if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
                String oldPathStr = user.getAvatarUrl().startsWith("/")
                        ? user.getAvatarUrl().substring(1)
                        : user.getAvatarUrl();
                java.nio.file.Path oldFilePath = java.nio.file.Paths.get(oldPathStr);

                try {
                    java.nio.file.Files.deleteIfExists(oldFilePath);
                } catch (IOException e) {
                    System.err.println("Не удалось удалить старый файл: " + e.getMessage());
                }
            }

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            java.nio.file.Path newFilePath = rootPath.resolve(fileName);

            java.nio.file.Files.copy(file.getInputStream(), newFilePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            user.setAvatarUrl("/" + uploadDir + fileName);
            userRepository.save(user);

            return UserMapper.mapToDto(user);

        } catch (IOException e) {
            throw new FilesException("Ошибка при сохранении файла: " + e.getMessage());
        }
    }
}
