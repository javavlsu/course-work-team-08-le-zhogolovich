package ru.vlsu.ispi.movieproject.exception;

import org.springframework.http.HttpStatus;

public class SelfFollowException extends BaseException {
    public SelfFollowException() {
        super("Невозможно подписаться на самого себя", HttpStatus.CONFLICT);
    }
}
