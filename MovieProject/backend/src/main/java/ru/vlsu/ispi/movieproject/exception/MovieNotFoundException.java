package ru.vlsu.ispi.movieproject.exception;

public class MovieNotFoundException extends RuntimeException {
    public MovieNotFoundException(Long id) {
        super("Фильм c id: " + id + " не найден");
    }
}
