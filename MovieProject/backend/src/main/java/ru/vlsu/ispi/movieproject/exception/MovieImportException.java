package ru.vlsu.ispi.movieproject.exception;

import org.springframework.http.HttpStatus;

public class MovieImportException extends BaseException {
    public MovieImportException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
