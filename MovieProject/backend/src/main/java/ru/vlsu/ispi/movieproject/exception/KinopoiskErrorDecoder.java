package ru.vlsu.ispi.movieproject.exception;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
public class KinopoiskErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        int status = response.status();
        String body = null;

        try (InputStream is = response.body().asInputStream()) {
            body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.warn("Не удалось прочитать body ответа", e);
        }

        log.error("Kinopoisk API error. method={}, status={}, body={}", methodKey, status, body);

        return new KinopoiskApiException("Ошибка при обращении к Kinopoisk API", status);
    }
}
