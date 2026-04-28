package ru.vlsu.ispi.movieproject.exception;

import org.springframework.http.HttpStatus;

public class CompilationNotFoundException extends BaseException{
    public CompilationNotFoundException(Long id) {
        super("Подборка c id: " + id + " не найдена", HttpStatus.NOT_FOUND);
    }

    public CompilationNotFoundException() {
        super("Одна или несколько подборок не найдены", HttpStatus.NOT_FOUND);
    }
}
