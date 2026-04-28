package ru.vlsu.ispi.movieproject.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.vlsu.ispi.movieproject.exception.FilesException;
import ru.vlsu.ispi.movieproject.service.FileStorageService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageServiceImpl implements FileStorageService {
    @Value("${file.base-dir}")
    private String baseUploadDir;

    @Override
    public String upload(MultipartFile file, String directory) {
        if (file == null || file.isEmpty()) {
            throw new FilesException("Файл пуст");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new FilesException("Файл должен быть изображением");
        }

        try {
            Path dirPath = Paths.get(baseUploadDir + directory);

            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = dirPath.resolve(fileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return "/" + baseUploadDir + directory + "/" + fileName;

        } catch (IOException e) {
            throw new FilesException(e.getMessage());
        }
    }

    @Override
    public void delete(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) return;

        try {
            String pathStr = fileUrl.startsWith("/")
                    ? fileUrl.substring(1)
                    : fileUrl;

            Path path = Paths.get(pathStr);
            Files.deleteIfExists(path);

        } catch (IOException e) {
            log.warn("Не удалось удалить файл: {}", e.getMessage());
        }
    }
}
