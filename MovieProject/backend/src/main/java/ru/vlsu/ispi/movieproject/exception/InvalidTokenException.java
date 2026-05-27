package ru.vlsu.ispi.movieproject.exception;

import org.springframework.http.HttpStatus;

public class InvalidTokenException extends BaseException{
    public InvalidTokenException() {
        super("Токен недействителен", HttpStatus.UNAUTHORIZED);
    }
}
