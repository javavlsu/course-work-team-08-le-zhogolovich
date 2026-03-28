package ru.vlsu.ispi.movieproject.exception;

import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends BaseException{
    public UserAlreadyExistsException() {
        super("Пользователь с таким email уже существует", HttpStatus.FORBIDDEN);
    }
}
