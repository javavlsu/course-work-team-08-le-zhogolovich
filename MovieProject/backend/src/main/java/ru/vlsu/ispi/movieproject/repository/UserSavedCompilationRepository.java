package ru.vlsu.ispi.movieproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vlsu.ispi.movieproject.model.UserSavedCompilation;
import ru.vlsu.ispi.movieproject.model.User;
import ru.vlsu.ispi.movieproject.model.Compilation;

import java.util.List;

/**
 * Репозиторий для работы с сохранёнными коллекциями пользователей
 * ({@link UserSavedCompilation})
 *
 * <p>Позволяет выполнять стандартные CRUD-операции,
 * а также получать сохранённые коллекции по пользователю
 * и проверять факт сохранения коллекции</p>
 *
 * <p>Реализация создаётся автоматически Spring Data JPA.</p>

 */
public interface UserSavedCompilationRepository
        extends JpaRepository<UserSavedCompilation, Long> {

    /**
     * Возвращает все коллекции, сохранённые указанным пользователем.
     *
     * @param user пользователь
     * @return список сохранённых коллекций
     */
    List<UserSavedCompilation> findByUser(User user);

    /**
     * Проверяет, сохранил ли пользователь указанную коллекцию.
     *
     * @param user пользователь
     * @param compilation коллекция
     * @return true — если коллекция сохранена пользователем
     */
    boolean existsByUserAndCompilation(User user, Compilation compilation);

    /**
     * Удаляет запись о сохранении коллекции пользователем.
     *
     * @param user пользователь
     * @param compilation коллекция
     */
    void deleteByUserAndCompilation(User user, Compilation compilation);
}