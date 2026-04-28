package ru.vlsu.ispi.movieproject.exception;

import org.springframework.http.HttpStatus;

public class MovieIsNotInCompilationException extends BaseException {
    public MovieIsNotInCompilationException() {
        super("Фильма нет в подборке", HttpStatus.CONFLICT);
    }
}
