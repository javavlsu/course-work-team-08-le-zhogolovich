package ru.vlsu.ispi.movieproject.exception;

import org.springframework.http.HttpStatus;

public class AlreadyFollowedException extends BaseException {
    public AlreadyFollowedException() {
        super("Вы уже подписаны на этого пользователя", HttpStatus.CONFLICT);
    }
}
