package ru.vlsu.ispi.movieproject.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class KinopoiskApiException extends BaseException {
    private final int apiStatusCode;
    public KinopoiskApiException(String message, int apiStatusCode) {
        super(message, HttpStatus.BAD_GATEWAY);
        this.apiStatusCode = apiStatusCode;
    }

}
