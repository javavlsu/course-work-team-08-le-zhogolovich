package ru.vlsu.ispi.movieproject.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.vlsu.ispi.movieproject.model.Review;
import ru.vlsu.ispi.movieproject.projection.ReviewProjection;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с сущностью {@link Review}.
 *
 * <p>
 * Представляет пользовательские рецензии на фильмы
 * Предоставляет стандартные CRUD-операции,
 * автоматически реализуемые Spring Data JPA
 * через интерфейс {@link JpaRepository}.
 * </p>
 *
 * <p>
 * Доступные операции (реализуются автоматически):
 * <ul>
 *     <li>save(Review) — создание и обновление рецензии</li>
 *     <li>findById(Long) — поиск по идентификатору</li>
 *     <li>findAll() — получение всех рецензий</li>
 *     <li>deleteById(Long) — удаление рецензии</li>
 *     <li>existsById(Long) — проверка существования</li>
 * </ul>
 * </p>
 *
 *  * <p>
 *  * Дополнительно содержит методы поиска по внешним идентификаторам
 *  * </p>
 */
public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("""
        SELECT 
            r.id as id,
            m.id as movieId,
            u.username as authorName,
            u.avatarUrl as authorAvatar,
            r.title as title,
            r.content as content,
            r.status as status,
            r.createdAt as createdAt,
    
            COUNT(rl) as likesCount,
    
            CASE 
                WHEN SUM(CASE WHEN rl.user.id = :currentUserId THEN 1 ELSE 0 END) > 0 
                THEN true ELSE false 
            END as isLikedByCurrentUser,
    
            m.posterUrl as movieCover
    
        FROM Review r
        JOIN r.author u
        JOIN r.movie m
        LEFT JOIN ReviewLike rl ON rl.review.id = r.id
    
        WHERE (:userId IS NULL OR u.id = :userId)
    
        GROUP BY 
            r.id, m.id, u.username, u.avatarUrl,
            r.title, r.content, r.status, r.createdAt, m.posterUrl
    
        ORDER BY r.createdAt DESC
    """)
    List<ReviewProjection> findReviews(Long userId, Long currentUserId);

    @Query(
            value = """
        SELECT 
            r.id as id,
            m.id as movieId,
            u.username as authorName,
            u.avatarUrl as authorAvatar,
            r.title as title,
            r.content as content,
            r.status as status,
            r.createdAt as createdAt,

            COUNT(rl) as likesCount,

            CASE 
                WHEN SUM(CASE WHEN rl.user.id = :currentUserId THEN 1 ELSE 0 END) > 0 
                THEN true ELSE false 
            END as isLikedByCurrentUser,

            m.posterUrl as movieCover

        FROM Review r
        JOIN r.author u
        JOIN r.movie m
        LEFT JOIN ReviewLike rl ON rl.review.id = r.id

        GROUP BY 
            r.id, m.id, u.username, u.avatarUrl,
            r.title, r.content, r.status, r.createdAt, m.posterUrl

        ORDER BY r.createdAt DESC
    """,
            countQuery = "SELECT COUNT(r) FROM Review r"
    )
    Page<ReviewProjection> findAllReviews(Pageable pageable, Long currentUserId);

    @Query("""
        SELECT 
            r.id as id,
            m.id as movieId,
            u.username as authorName,
            u.avatarUrl as authorAvatar,
            r.title as title,
            r.content as content,
            r.status as status,
            r.createdAt as createdAt,
    
            COUNT(rl) as likesCount,
    
            CASE 
                WHEN SUM(CASE WHEN rl.user.id = :currentUserId THEN 1 ELSE 0 END) > 0 
                THEN true ELSE false 
            END as isLikedByCurrentUser,
    
            m.posterUrl as movieCover
    
        FROM Review r
        JOIN r.author u
        JOIN r.movie m
        LEFT JOIN ReviewLike rl ON rl.review.id = r.id
    
        WHERE r.id = :reviewId
    
        GROUP BY 
            r.id, m.id, u.username, u.avatarUrl,
            r.title, r.content, r.status, r.createdAt, m.posterUrl
    """)
    Optional<ReviewProjection> findReviewById(Long reviewId, Long currentUserId);
}