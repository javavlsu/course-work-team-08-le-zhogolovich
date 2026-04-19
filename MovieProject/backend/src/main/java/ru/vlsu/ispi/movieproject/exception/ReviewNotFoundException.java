package ru.vlsu.ispi.movieproject.exception;

import org.springframework.http.HttpStatus;

public class ReviewNotFoundException extends BaseException {
    public ReviewNotFoundException(Long id) {
        super("Рецензия с id: " + id + "не найдена", HttpStatus.NOT_FOUND);
    }
}
