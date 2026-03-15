package ru.vlsu.ispi.movieproject.exception;

public class UserAlreadyExistsException extends BaseException{
    public UserAlreadyExistsException() {
        super("Пользователь с таким email уже существует");
    }
}
