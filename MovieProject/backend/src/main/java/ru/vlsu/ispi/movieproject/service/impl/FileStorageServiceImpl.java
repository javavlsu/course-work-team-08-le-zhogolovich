package ru.vlsu.ispi.movieproject.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.vlsu.ispi.movieproject.exception.FilesException;
import ru.vlsu.ispi.movieproject.service.FileStorageService;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageServiceImpl implements FileStorageService {
    @Value("${file.upload-dir}")
    private String baseUploadDir;

    @Value("${file.poster-dir}")
    private String basePosterDir;

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
            Path dirPath = Paths.get(baseUploadDir, directory);

            String extension = getExtensionFromContentType(contentType);
            String fileName = UUID.randomUUID() + extension;

            Path filePath = dirPath.resolve(fileName);

            try (InputStream in = file.getInputStream()) {
                Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
            }

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

    @Override
    public String downloadPosterAndSave(String url) {
        if (url == null || url.isBlank() || url.contains("no-poster")) {
            return null;
        }

        Path dirPath = Paths.get(basePosterDir);

        try (InputStream in = new URL(url).openStream()){
            String contentType = URLConnection.guessContentTypeFromStream(in);

            if (contentType == null || !contentType.startsWith("image/")) {
                return null;
            }

            String extension = getExtensionFromContentType(contentType);
            if (extension == null) {
                return null;
            }

            String filename = UUID.randomUUID() + extension;
            Path target = dirPath.resolve(filename);
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);

            return "/" + basePosterDir + filename;
        } catch (Exception e) {
            return null;
        }
    }

    private String getExtensionFromContentType(String contentType) {
        return switch (contentType) {
            case "image/png" -> ".png";
            case "image/jpeg" -> ".jpg";
            case "image/jpg" -> ".jpg";
            case "image/webp" -> ".webp";
            case "image/gif" -> ".gif";
            default -> ".jpg";
        };
    }
}
