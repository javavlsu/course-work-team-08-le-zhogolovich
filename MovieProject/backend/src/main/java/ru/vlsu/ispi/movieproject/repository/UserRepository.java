package ru.vlsu.ispi.movieproject.repository;

import ru.vlsu.ispi.movieproject.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Репозиторий для работы с сущностью {@link User}.
 *
 * <p>
 * Предоставляет стандартные CRUD-операции для пользователей,
 * а также методы поиска по уникальным полям.
 * Реализация интерфейса автоматически создаётся Spring Data JPA
 * </p>
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Находит пользователя по адресу электронной почты
     *
     * @param email адрес электронной почты
     * @return Optional с найденным пользователем
     *         или Optional.empty(), если пользователь не найден
     */
    Optional<User> findByEmail(String email);

    /**
     * Проверяет существование пользователя с указанным email.
     *
     * @param email адрес электронной почты
     * @return true — если пользователь существует, иначе false
     */
    boolean existsByEmail(String email);

    Optional<User> findByUsername(String username);
}
