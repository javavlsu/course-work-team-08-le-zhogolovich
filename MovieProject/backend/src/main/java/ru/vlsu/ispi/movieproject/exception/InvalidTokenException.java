package ru.vlsu.ispi.movieproject.exception;

public class InvalidTokenException extends BaseException{
    public InvalidTokenException() {
        super("Токен недействителен");
    }
}
