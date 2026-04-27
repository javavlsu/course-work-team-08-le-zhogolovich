package ru.vlsu.ispi.movieproject.repository;

import org.springframework.data.jpa.repository.Query;
import ru.vlsu.ispi.movieproject.model.User;
import ru.vlsu.ispi.movieproject.model.UserFollow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

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

    @Query("""
        SELECT CASE WHEN COUNT(uf) > 0 THEN true ELSE false END
        FROM UserFollow uf
        WHERE uf.follower.id = :followerId 
          AND uf.followedUser.id = :followedUserId
          AND uf.follower.deleted = false 
          AND uf.followedUser.deleted = false
    """)
    boolean existsActiveFollow(Long followerId, Long followedUserId);

    @Query("""
        SELECT uf FROM UserFollow uf
        WHERE uf.follower.id = :followerId 
          AND uf.followedUser.id = :followedUserId
          AND uf.follower.deleted = false 
          AND uf.followedUser.deleted = false
    """)
    Optional<UserFollow> findActiveFollow(Long followerId, Long followedUserId);

    @Query("""
        SELECT uf.follower FROM UserFollow uf
        WHERE uf.followedUser.id = :userId
            AND uf.follower.deleted = false
    """)
    List<User> findFollowers(Long userId);

    @Query("""
        SELECT uf.followedUser FROM UserFollow uf
        WHERE uf.follower.id = :userId
            AND uf.followedUser.deleted = false
    """)
    List<User> findFollowings(Long userId);

}