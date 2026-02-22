package ru.vlsu.ispi.movieproject.service.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.vlsu.ispi.movieproject.dto.JwtAuthenticationDto;

@Component
public class JwtService {
    @Value("BnaqHVf6SlMBjYcB5bPvcXLX3LCSxvfZW09ZYPPBm1K")
    private String jwtSecret;

//    public JwtAuthenticationDto generateAuthToken(String email) {
//        JwtAuthenticationDto jwtDto = new JwtAuthenticationDto();
//        jwtDto.setToken(generateJwtToken(email));
//    }
//
//    private String generateJwtToken(String email) {
//        return new String();
//    }
}
