package ru.vlsu.ispi.movieproject.exception;

import org.springframework.http.HttpStatus;

public class GenreMapException extends BaseException {
  public GenreMapException(String genre) {
    super("Ошибка маппинга жанра: " + genre, HttpStatus.BAD_REQUEST);
  }
}
