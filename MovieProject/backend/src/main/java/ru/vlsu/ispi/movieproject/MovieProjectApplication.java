package ru.vlsu.ispi.movieproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MovieProjectApplication {
    public static void main(String[] args) {
        SpringApplication.run(MovieProjectApplication.class, args);
    }
}
