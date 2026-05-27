package ru.vlsu.ispi.movieproject.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorageService {
    String upload(MultipartFile file, String directory);
    void delete(String fileUrl);
    String downloadPosterAndSave(String url) throws IOException;
}
