package ru.vlsu.ispi.movieproject.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.vlsu.ispi.movieproject.exception.KinopoiskErrorDecoder;

@Configuration
public class FeignConfig {
    @Value("${kinopoisk.api.key}")
    private String apiKey;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template ->
            template.header("X-API-KEY", apiKey);
    }

    @Bean
    public KinopoiskErrorDecoder errorDecoder() {
        return new KinopoiskErrorDecoder();
    }
}
