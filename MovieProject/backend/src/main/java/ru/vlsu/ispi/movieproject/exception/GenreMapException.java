package ru.vlsu.ispi.movieproject.exception;

public class GenreMapException extends RuntimeException {
  public GenreMapException(String genre) {
    super("Ошибка маппинга жанра: " + genre);
  }
}
