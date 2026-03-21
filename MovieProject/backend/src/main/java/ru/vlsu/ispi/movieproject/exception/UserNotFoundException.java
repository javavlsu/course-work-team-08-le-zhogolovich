package ru.vlsu.ispi.movieproject.exception;

public class UserNotFoundException extends BaseException{
    public UserNotFoundException(String login) {
        super("Пользователь" + login + "не найден.");
    }
}
