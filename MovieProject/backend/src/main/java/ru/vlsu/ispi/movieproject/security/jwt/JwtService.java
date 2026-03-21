package ru.vlsu.ispi.movieproject.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.vlsu.ispi.movieproject.dto.auth.JwtAuthenticationDto;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtService {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.access-exp}")
    private long accessExp;

    @Value("${jwt.refresh-exp}")
    private long refreshExp;

    public JwtAuthenticationDto generateAuthToken(Long id, String role) {
        JwtAuthenticationDto jwtDto = new JwtAuthenticationDto();
        jwtDto.setToken(generateJwtToken(id, role));
        jwtDto.setRefreshToken(generateRefreshToken(id));

        return jwtDto;
    }

    public JwtAuthenticationDto refreshAuthToken(Long id, String refreshToken) {
        JwtAuthenticationDto jwtDto = new JwtAuthenticationDto();
        jwtDto.setToken(generateJwtToken(id, extractRole(refreshToken)));
        jwtDto.setRefreshToken(refreshToken);

        return jwtDto;
    }

    private String generateJwtToken(Long id, String role) {
        Date date = Date.from(LocalDateTime.now().plusHours(accessExp).atZone(ZoneId.systemDefault()).toInstant());
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        claims.put("userId", id);

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(date)
                .signWith(getSignKey())
                .compact();
    }

    private String generateRefreshToken(Long id) {
        Date date = Date.from(LocalDateTime.now().plusHours(refreshExp).atZone(ZoneId.systemDefault()).toInstant());
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", id);

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(date)
                .signWith(getSignKey())
                .compact();
    }

    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    public Long extractUserId(String token) {
        return extractAllClaims(token).get("userId", Long.class);
    }

    public boolean isTokenValid(String token) {
        return !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
