package ru.vlsu.ispi.movieproject.exception;

public class CountryMapException extends RuntimeException {
    public CountryMapException(String country) {
        super("Ошибка маппинга жанра: " + country);
    }
}
