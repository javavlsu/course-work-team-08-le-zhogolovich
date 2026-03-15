package ru.vlsu.ispi.movieproject.exception;

public class UserNotFoundException extends BaseException{
    public UserNotFoundException(String email) {
        super("Пользователь с email:" + email + "не найден.");
    }
}
