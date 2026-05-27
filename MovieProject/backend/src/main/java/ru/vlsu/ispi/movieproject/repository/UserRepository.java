package ru.vlsu.ispi.movieproject.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.vlsu.ispi.movieproject.model.User;

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
    boolean existsByEmailAndDeletedFalse(String email);

    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameAndDeletedFalse(String username);
    Optional<User> findByIdAndDeletedFalse(Long id);

    @Query("""
        SELECT u
        FROM User u
        WHERE u.deleted = false
        AND (
            :query IS NULL
            OR :query = ''
            OR LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%'))
        )
    """)
    Page<User> search(String query, Pageable pageable);
}
