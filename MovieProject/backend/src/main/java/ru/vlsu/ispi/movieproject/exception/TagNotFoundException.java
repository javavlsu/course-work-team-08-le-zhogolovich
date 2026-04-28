package ru.vlsu.ispi.movieproject.exception;

import org.springframework.http.HttpStatus;

public class TagNotFoundException extends BaseException {
    public TagNotFoundException(Long id) {
        super("Тег с id: " + id + " не найден", HttpStatus.NOT_FOUND);
    }
}
