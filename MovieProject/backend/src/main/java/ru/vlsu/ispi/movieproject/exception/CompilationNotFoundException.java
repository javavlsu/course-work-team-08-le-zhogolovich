package ru.vlsu.ispi.movieproject.exception;

import org.springframework.http.HttpStatus;

public class CompilationNotFoundException extends BaseException{
    public CompilationNotFoundException(Long id) {
        super("Подборка c id: " + id + " не найдена", HttpStatus.NOT_FOUND);
    }

}
