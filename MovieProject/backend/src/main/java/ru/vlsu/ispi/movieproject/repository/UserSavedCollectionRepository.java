package ru.vlsu.ispi.movieproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vlsu.ispi.movieproject.model.UserSavedCollection;
import ru.vlsu.ispi.movieproject.model.User;
import ru.vlsu.ispi.movieproject.model.Collection;

import java.util.List;

/**
 * Репозиторий для работы с сохранёнными коллекциями пользователей
 * ({@link UserSavedCollection})
 *
 * <p>Позволяет выполнять стандартные CRUD-операции,
 * а также получать сохранённые коллекции по пользователю
 * и проверять факт сохранения коллекции</p>
 *
 * <p>Реализация создаётся автоматически Spring Data JPA.</p>

 */
public interface UserSavedCollectionRepository
        extends JpaRepository<UserSavedCollection, Long> {

    /**
     * Возвращает все коллекции, сохранённые указанным пользователем.
     *
     * @param user пользователь
     * @return список сохранённых коллекций
     */
    List<UserSavedCollection> findByUser(User user);

    /**
     * Проверяет, сохранил ли пользователь указанную коллекцию.
     *
     * @param user пользователь
     * @param collection коллекция
     * @return true — если коллекция сохранена пользователем
     */
    boolean existsByUserAndCollection(User user, Collection collection);

    /**
     * Удаляет запись о сохранении коллекции пользователем.
     *
     * @param user пользователь
     * @param collection коллекция
     */
    void deleteByUserAndCollection(User user, Collection collection);
}