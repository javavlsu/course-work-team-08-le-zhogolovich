package ru.vlsu.ispi.movieproject.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BaseException{
    public UserNotFoundException() {
        super("Пользователь не найден", HttpStatus.NOT_FOUND);
    }
}
