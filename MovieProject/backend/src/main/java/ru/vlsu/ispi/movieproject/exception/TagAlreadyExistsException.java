package ru.vlsu.ispi.movieproject.exception;

import org.springframework.http.HttpStatus;

public class TagAlreadyExistsException extends BaseException {
    public TagAlreadyExistsException() {
        super("Такой тег уже существует", HttpStatus.CONFLICT);
    }
}
