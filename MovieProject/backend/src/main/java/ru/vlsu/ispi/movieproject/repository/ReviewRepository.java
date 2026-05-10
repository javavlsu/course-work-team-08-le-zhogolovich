package ru.vlsu.ispi.movieproject.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.vlsu.ispi.movieproject.model.Review;
import ru.vlsu.ispi.movieproject.projection.ReviewProjection;

import java.time.LocalDateTime;
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
            m.name as movieName,
            u.username as authorName,
            u.avatarUrl as authorAvatar,
            r.title as title,
            r.content as content,
            r.status as status,
            r.createdAt as createdAt,
            r.likesCount as likesCount,
                
            CASE
                WHEN :currentUserId IS NULL THEN false
                WHEN EXISTS (
                    SELECT 1
                    FROM ReviewLike rl2
                    WHERE rl2.review.id = r.id
                      AND rl2.user.id = :currentUserId
                )
                THEN true ELSE false
            END as isLikedByCurrentUser,
    
            m.posterUrl as movieCover
    
        FROM Review r
        JOIN r.author u
        JOIN r.movie m
    
        WHERE (:userId IS NULL OR u.id = :userId)
            AND (
              r.status = 'PUBLISHED'
              OR r.author.id = :currentUserId
            )
    
        ORDER BY r.createdAt DESC
    """)
    List<ReviewProjection> findUserReviews(Long userId, Long currentUserId);

    @Query("""
        SELECT 
            r.id as id,
            m.id as movieId,
            m.name as movieName,
            u.username as authorName,
            u.avatarUrl as authorAvatar,
            r.title as title,
            r.content as content,
            r.status as status,
            r.createdAt as createdAt,
            r.likesCount as likesCount,
    
            CASE
                WHEN :currentUserId IS NULL THEN false
                WHEN EXISTS (
                    SELECT 1
                    FROM ReviewLike rl2
                    WHERE rl2.review.id = r.id
                      AND rl2.user.id = :currentUserId
                )
                THEN true ELSE false
            END as isLikedByCurrentUser,
    
            m.posterUrl as movieCover
    
        FROM Review r
        JOIN r.author u
        JOIN r.movie m
    
        WHERE r.id = :reviewId
          AND (
              r.status = 'PUBLISHED'
              OR r.author.id = :currentUserId
          )
    """)
    Optional<ReviewProjection> findReviewById(Long reviewId, Long currentUserId);

    @Query("""
        SELECT 
            r.id as id,
            m.id as movieId,
            m.name as movieName,
            u.username as authorName,
            u.avatarUrl as authorAvatar,
            r.title as title,
            r.content as content,
            r.status as status,
            r.createdAt as createdAt,
            r.likesCount as likesCount,
    
            CASE
                WHEN :currentUserId IS NULL THEN false
                WHEN EXISTS (
                    SELECT 1
                    FROM ReviewLike rl2
                    WHERE rl2.review.id = r.id
                      AND rl2.user.id = :currentUserId
                )
                THEN true ELSE false
            END as isLikedByCurrentUser,
    
            m.posterUrl as movieCover

        FROM Review r
        JOIN r.author u
        JOIN r.movie m
        JOIN ReviewLike rlu ON rlu.review.id = r.id
    
        WHERE rlu.user.id = :userId
          AND (
              r.status = 'PUBLISHED'
              OR r.author.id = :currentUserId
          )
    """)
    List<ReviewProjection> findAllLikedByUserId(Long userId, Long currentUserId);

    @Query("""
        SELECT 
            r.id as id,
            m.id as movieId,
            m.name as movieName,
            u.username as authorName,
            u.avatarUrl as authorAvatar,
            r.title as title,
            r.content as content,
            r.status as status,
            r.createdAt as createdAt,
            r.likesCount as likesCount,
    
            CASE
                WHEN :currentUserId IS NULL THEN false
                WHEN EXISTS (
                    SELECT 1
                    FROM ReviewLike rl2
                    WHERE rl2.review.id = r.id
                      AND rl2.user.id = :currentUserId
                )
                THEN true ELSE false
            END as isLikedByCurrentUser,
    
            m.posterUrl as movieCover
    
        FROM Review r
        JOIN r.author u
        JOIN r.movie m
    
        WHERE m.id = :movieId
          AND (
              r.status = 'PUBLISHED'
              OR r.author.id = :currentUserId
          )
    """)
    List<ReviewProjection> findReviewsByMovieId(Long movieId, Long currentUserId);

    @Query("""
        SELECT 
            r.id as id,
            m.id as movieId,
            m.name as movieName,
            u.username as authorName,
            u.avatarUrl as authorAvatar,
            r.title as title,
            r.content as content,
            r.status as status,
            r.createdAt as createdAt,
            r.likesCount as likesCount,
    
            CASE
                WHEN :currentUserId IS NULL THEN false
                WHEN EXISTS (
                    SELECT 1
                    FROM ReviewLike rl2
                    WHERE rl2.review.id = r.id
                      AND rl2.user.id = :currentUserId
                )
                THEN true ELSE false
            END as isLikedByCurrentUser,
    
            m.posterUrl as movieCover
    
        FROM Review r
        JOIN r.author u
        JOIN r.movie m
        LEFT JOIN ReviewLike rl 
            ON rl.review.id = r.id
            AND rl.createdAt >= :fromDate
    
        WHERE r.status = 'PUBLISHED'
    
        GROUP BY 
            r.id, m.id, m.name, u.username, u.avatarUrl,
            r.title, r.content, r.status, r.createdAt, m.posterUrl
        HAVING COUNT(rl) > 0
            
        ORDER BY COUNT(rl) DESC
    """)
    List<ReviewProjection> findTopReviewsLastWeek(LocalDateTime fromDate, Long currentUserId, Pageable pageable);

    @Query("""
        SELECT 
            r.id as id,
            m.id as movieId,
            m.name as movieName,
            u.username as authorName,
            u.avatarUrl as authorAvatar,
            r.title as title,
            r.content as content,
            r.status as status,
            r.createdAt as createdAt,
            r.likesCount as likesCount,
    
            CASE
                WHEN :currentUserId IS NULL THEN false
                WHEN EXISTS (
                    SELECT 1
                    FROM ReviewLike rl2
                    WHERE rl2.review.id = r.id
                      AND rl2.user.id = :currentUserId
                )
                THEN true ELSE false
            END as isLikedByCurrentUser,
    
            m.posterUrl as movieCover
    
        FROM Review r
        JOIN r.author u
        JOIN r.movie m
    
        WHERE r.status = 'PUBLISHED'
        AND (:excludeIds IS NULL OR r.id NOT IN :excludeIds)
    
        ORDER BY r.createdAt DESC
    """)
    List<ReviewProjection> findLatest(List<Long> excludeIds, Long currentUserId, Pageable pageable);

    @Query("""
        SELECT 
            r.id as id,
            m.id as movieId,
            m.name as movieName,
            u.username as authorName,
            u.avatarUrl as authorAvatar,
            r.title as title,
            r.content as content,
            r.status as status,
            r.createdAt as createdAt,
            r.likesCount as likesCount,
    
            CASE
                WHEN :currentUserId IS NULL THEN false
                WHEN EXISTS (
                    SELECT 1
                    FROM ReviewLike rl2
                    WHERE rl2.review.id = r.id
                      AND rl2.user.id = :currentUserId
                )
                THEN true ELSE false
            END as isLikedByCurrentUser,
    
            m.posterUrl as movieCover
    
        FROM Review r
        JOIN r.author u
        JOIN r.movie m
    
        WHERE (
            :query IS NULL
            OR :query = ''
            OR LOWER(r.title) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(m.name) LIKE LOWER(CONCAT('%', :query, '%'))
        )
        AND r.status = 'PUBLISHED'

        ORDER BY
            CASE
                WHEN :sortBy = 'title' AND :sortOrder = 'asc'
                THEN r.title
            END ASC,
    
            CASE
                WHEN :sortBy = 'title' AND :sortOrder = 'desc'
                THEN r.title
            END DESC,
    
            CASE
                WHEN :sortBy = 'popularity' AND :sortOrder = 'asc'
                THEN r.likesCount
            END ASC,
    
            CASE
                WHEN :sortBy = 'popularity' AND :sortOrder = 'desc'
                THEN r.likesCount
            END DESC,
                
            r.createdAt DESC
    """)
    Page<ReviewProjection> search(String query, String sortBy, String sortOrder, Long currentUserId, Pageable pageable);

    @Modifying
    @Query("""
        UPDATE Review r
        SET r.likesCount = r.likesCount + 1
        WHERE r.id = :id
    """)
    void incrementLikes(Long id);

    @Modifying
    @Query("""
        UPDATE Review r
        SET r.likesCount = r.likesCount - 1
        WHERE r.id = :id
    """)
    void decrementLikes(Long id);
}