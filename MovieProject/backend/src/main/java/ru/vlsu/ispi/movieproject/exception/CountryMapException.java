package ru.vlsu.ispi.movieproject.exception;

import org.springframework.http.HttpStatus;

public class CountryMapException extends BaseException {
    public CountryMapException(String country) {
        super("Ошибка маппинга жанра: " + country, HttpStatus.BAD_REQUEST);
    }
}
