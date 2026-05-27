package ru.vlsu.ispi.movieproject.exception;

import org.springframework.http.HttpStatus;

public class CommentNotFoundException extends BaseException {
    public CommentNotFoundException(Long id) {
        super("Комментарий с id: " + id + " не найден", HttpStatus.NOT_FOUND);
    }
}
