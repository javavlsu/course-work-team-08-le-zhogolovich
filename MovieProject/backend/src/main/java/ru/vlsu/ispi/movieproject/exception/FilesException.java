package ru.vlsu.ispi.movieproject.exception;

import org.springframework.http.HttpStatus;

public class FilesException extends BaseException {
    public FilesException(String message) {
        super("Ошибка при работе с файлами: " + message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
