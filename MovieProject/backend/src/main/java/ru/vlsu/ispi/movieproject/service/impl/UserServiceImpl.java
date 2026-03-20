package ru.vlsu.ispi.movieproject.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.vlsu.ispi.movieproject.dto.user.UserDto;
import ru.vlsu.ispi.movieproject.mapper.UserMapper;
import ru.vlsu.ispi.movieproject.model.User;
import ru.vlsu.ispi.movieproject.repository.UserRepository;
import ru.vlsu.ispi.movieproject.service.UserService;

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
    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        return UserMapper.mapToDto(user);
    }

    @Override
    public UserDto updateAvatar(String email, MultipartFile file) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (file.isEmpty()) {
            throw new RuntimeException("Файл пуст");
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
                } catch (java.io.IOException e) {
                    System.err.println("Не удалось удалить старый файл: " + e.getMessage());
                }
            }

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            java.nio.file.Path newFilePath = rootPath.resolve(fileName);

            java.nio.file.Files.copy(file.getInputStream(), newFilePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            user.setAvatarUrl("/" + uploadDir + fileName);
            userRepository.save(user);

            return UserMapper.mapToDto(user);

        } catch (java.io.IOException e) {
            throw new RuntimeException("Ошибка при работе с файлами", e);
        }
    }
}
