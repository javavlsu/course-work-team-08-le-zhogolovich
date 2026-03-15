package ru.vlsu.ispi.movieproject.repository;

import ru.vlsu.ispi.movieproject.model.UserFollow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Репозиторий для работы с сущностью {@link UserFollow}.
 *
 * <p>
 * Предоставляет доступ к данным подписок пользователей
 * Базовые CRUD-операции автоматически реализуются Spring Data JPA через {@link JpaRepository}.
 * </p>
 *
 * <p>
 * Дополнительно объявлены методы выборки, отражающие бизнес-логику подписок пользователей.
 * </p>
 */
public interface UserFollowRepository extends JpaRepository<UserFollow, Long> {

    /**
     * Возвращает список пользователей, на которых подписан указанный пользователь.
     *
     * @param followerId идентификатор подписчика
     * @return список подписок пользователя
     */
    List<UserFollow> findByFollowerId(Long followerId);

    /**
     * Возвращает список подписчиков пользователя.
     *
     * @param followedUserId идентификатор пользователя, на которого подписаны
     * @return список подписчиков
     */
    List<UserFollow> findByFollowedUserId(Long followedUserId);

    /**
     * Проверяет, подписан ли пользователь
     * на другого пользователя.
     *
     * @param followerId идентификатор подписчика
     * @param followedUserId идентификатор пользователя
     * @return true — если подписка существует
     */
    boolean existsByFollowerIdAndFollowedUserId(
            Long followerId,
            Long followedUserId
    );

    /**
     * Удаляет подписку пользователя.
     *
     * @param followerId идентификатор подписчика
     * @param followedUserId идентификатор пользователя
     */
    void deleteByFollowerIdAndFollowedUserId(
            Long followerId,
            Long followedUserId
    );
}