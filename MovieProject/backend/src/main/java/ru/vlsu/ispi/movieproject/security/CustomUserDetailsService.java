package ru.vlsu.ispi.movieproject.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.vlsu.ispi.movieproject.model.User;
import ru.vlsu.ispi.movieproject.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(login)
                .or(() -> userRepository.findByUsername(login))
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь" + login + "не найден."));

        if (user.getDeleted()) throw new UsernameNotFoundException("Пользователь" + login + "не найден.");

        return new CustomUserDetails(user);
    }

    public CustomUserDetails loadUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь с ID " + id + " не найден"));

        if (user.getDeleted()) {
            throw new UsernameNotFoundException("Пользователь с ID " + id + " не найден");
        }

        return new CustomUserDetails(user);
    }
}
