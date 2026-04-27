package ru.vlsu.ispi.movieproject.exception;

import org.springframework.http.HttpStatus;

public class FollowNotFoundException extends BaseException{
    public FollowNotFoundException() {
        super("Вы не подписаны на этого пользователя", HttpStatus.NOT_FOUND);
    }
}
