package ru.vlsu.ispi.movieproject.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import ru.vlsu.ispi.movieproject.security.jwt.JwtFilter;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        // preflight
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // публичные ресурсы
                        .requestMatchers("/uploads/**", "/posters/**").permitAll()
                        // документация
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        // auth
                        .requestMatchers("/auth/**").permitAll()
                        // movies
                        .requestMatchers(HttpMethod.GET, "/movies/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/movies/*/rating").authenticated()
                        .requestMatchers(HttpMethod.POST, "/movies/*/tags/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/movies/*/tags/*").hasRole("ADMIN")
                        // tags
                        .requestMatchers(HttpMethod.GET, "/tags/**").permitAll()
                        .requestMatchers("/tags/**").hasRole("ADMIN")
                        // genres
                        .requestMatchers(HttpMethod.GET, "/genres/**").permitAll()
                        // countries
                        .requestMatchers(HttpMethod.GET, "/countries/**").permitAll()
                        // compilations
                        .requestMatchers(HttpMethod.GET, "/compilations/**").permitAll()
                        // comments
                        .requestMatchers(HttpMethod.GET, "/comment/**").permitAll()
                        // reviews
                        .requestMatchers(HttpMethod.GET, "/reviews/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/reviews/*/like").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/reviews/*/like").permitAll()
                        .requestMatchers(HttpMethod.POST, "/reviews").hasAnyRole("ADMIN", "REVIEWER")
                        .requestMatchers(HttpMethod.PATCH, "/reviews/*").hasAnyRole("ADMIN", "REVIEWER")
                        .requestMatchers(HttpMethod.DELETE, "/reviews/*").hasAnyRole("ADMIN", "REVIEWER")
                                                // import
                        .requestMatchers("/api/import/**").hasRole("ADMIN")
                        // user
                        .requestMatchers("/users/*/role").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/users").permitAll()
                        .requestMatchers("/users/me").authenticated()
                        .requestMatchers(HttpMethod.GET, "/users/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/users/id/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/users/*/followers").permitAll()
                        .requestMatchers(HttpMethod.GET, "/users/*/followings").permitAll()

                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS","PATCH"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Cache-Control"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
    }
