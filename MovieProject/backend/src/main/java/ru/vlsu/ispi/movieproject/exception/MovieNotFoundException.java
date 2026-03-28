package ru.vlsu.ispi.movieproject.exception;

import org.springframework.http.HttpStatus;

public class MovieNotFoundException extends BaseException {
    public MovieNotFoundException(Long id) {
        super("Фильм c id: " + id + " не найден", HttpStatus.NOT_FOUND);
    }
}
