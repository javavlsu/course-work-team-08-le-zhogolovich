package ru.vlsu.ispi.movieproject.exception;

import org.springframework.http.HttpStatus;

public class MovieAlreadyInCompilationException extends BaseException {
    public MovieAlreadyInCompilationException() {
        super("Фильм уже есть в этой подборке", HttpStatus.CONFLICT);
    }
}
