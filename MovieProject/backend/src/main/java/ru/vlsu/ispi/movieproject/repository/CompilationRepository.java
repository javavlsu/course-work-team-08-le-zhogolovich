package ru.vlsu.ispi.movieproject.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.vlsu.ispi.movieproject.model.Compilation;
import ru.vlsu.ispi.movieproject.projection.CompilationProjection;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с сущностью {@link Compilation}.
 *
 * <p>
 * Предоставляет стандартные CRUD-операции, автоматически реализуемые Spring Data JPA
 * через интерфейс {@link JpaRepository}
 * </p>
 *
 * <p>
 * Доступные операции (реализуются автоматически):
 * <ul>
 *     <li>save(Collection) — создание и обновление коллекции</li>
 *     <li>findById(Long) — поиск по идентификатору</li>
 *     <li>findAll() — получение всех коллекций</li>
 *     <li>deleteById(Long) — удаление коллекции</li>
 *     <li>existsById(Long) — проверка существования</li>
 * </ul>
 * </p>
 *
 * <p>
 * Дополнительно содержит методы поиска коллекций,
 * автоматически генерируемые Spring Data JPA
 * на основе имени метода.
 * </p>
 */
public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    @Query("""
        SELECT c FROM Compilation c
        LEFT JOIN FETCH c.movies
        WHERE c.id = :id
        AND (c.isPublic = true OR c.author.id = :currentUserId)
    """)
    Optional<Compilation> findByIdWithMovies(Long id, Long currentUserId);

    @Query("""
    SELECT 
        c.id as id,
        c.title as title,
        c.description as description,
        c.isPublic as isPublic,
        c.coverUrl as coverUrl,
    
        c.author.id as authorId,
        c.author.username as authorName,
    
        (SELECT COUNT(cl) 
         FROM CompilationLike cl 
         WHERE cl.compilation.id = c.id) as likesCount,
    
        CASE 
            WHEN :currentUserId IS NULL THEN false
            WHEN EXISTS (
                SELECT 1 
                FROM CompilationLike cl2 
                WHERE cl2.compilation.id = c.id 
                  AND cl2.user.id = :currentUserId
            )
            THEN true ELSE false
        END as likedByUser,
    
        (SELECT COUNT(cs) 
         FROM CompilationSubscription cs 
         WHERE cs.compilation.id = c.id) as subscribersCount,
    
        CASE 
            WHEN :currentUserId IS NULL THEN false
            WHEN EXISTS (
                SELECT 1 
                FROM CompilationSubscription cs2 
                WHERE cs2.compilation.id = c.id 
                  AND cs2.user.id = :currentUserId
            )
            THEN true ELSE false
        END as isSubscribed
    
    FROM Compilation c
    WHERE c.author.id = :authorId
    AND (
        c.isPublic = true OR c.author.id = :currentUserId
        )
    """)
    List<CompilationProjection> findAllByAuthorId(Long authorId, Long currentUserId);

    @Query("""
    SELECT 
        c.id as id,
        c.title as title,
        c.description as description,
        c.isPublic as isPublic,
        c.coverUrl as coverUrl,

        c.author.id as authorId,
        c.author.username as authorName,

        (SELECT COUNT(cl)
         FROM CompilationLike cl 
         WHERE cl.compilation.id = c.id) as likesCount,

        CASE 
            WHEN :currentUserId IS NULL THEN false
            WHEN EXISTS (
                SELECT 1 
                FROM CompilationLike cl2 
                WHERE cl2.compilation.id = c.id 
                  AND cl2.user.id = :currentUserId
            )
            THEN true ELSE false
        END as likedByUser,

        (SELECT COUNT(cs) 
         FROM CompilationSubscription cs 
         WHERE cs.compilation.id = c.id) as subscribersCount,

        true as isSubscribed

    FROM Compilation c
    JOIN CompilationSubscription cs ON cs.compilation.id = c.id
    WHERE cs.user.id = :userId
    AND (
        c.isPublic = true OR c.author.id = :currentUserId
        )
    """)
    List<CompilationProjection> findAllSubscribedByUserId(Long userId, Long currentUserId);

    @Query("""
    SELECT 
        c.id as id,
        c.title as title,
        c.description as description,
        c.isPublic as isPublic,
        c.coverUrl as coverUrl,
    
        c.author.id as authorId,
        c.author.username as authorName,
    
        (SELECT COUNT(cl) 
         FROM CompilationLike cl 
         WHERE cl.compilation.id = c.id) as likesCount,
    
        CASE 
            WHEN :userId IS NULL THEN false
            WHEN EXISTS (
                SELECT 1 
                FROM CompilationLike cl2 
                WHERE cl2.compilation.id = c.id 
                  AND cl2.user.id = :userId
            )
            THEN true ELSE false
        END as likedByUser,
    
        (SELECT COUNT(cs) 
         FROM CompilationSubscription cs 
         WHERE cs.compilation.id = c.id) as subscribersCount,
    
        CASE 
            WHEN :userId IS NULL THEN false
            WHEN EXISTS (
                SELECT 1 
                FROM CompilationSubscription cs2 
                WHERE cs2.compilation.id = c.id 
                  AND cs2.user.id = :userId
            )
            THEN true ELSE false
        END as isSubscribed
    
    FROM Compilation c
    WHERE c.isPublic = true
    """)
    Page<CompilationProjection> findAllView(Pageable pageable, Long userId);

    @Query("""
        SELECT 
            c.id as id,
            c.title as title,
            c.description as description,
            c.isPublic as isPublic,
            c.coverUrl as coverUrl,
        
            c.author.id as authorId,
    
            CASE 
                WHEN c.author IS NULL THEN 'Удалённый пользователь'
                WHEN c.author.deleted = true THEN 'Удалённый пользователь'
                ELSE c.author.username
            END as authorName,
        
            (SELECT COUNT(cl) 
             FROM CompilationLike cl 
             WHERE cl.compilation.id = c.id) as likesCount,
        
            CASE 
                WHEN :userId IS NULL THEN false
                WHEN EXISTS (
                    SELECT 1 
                    FROM CompilationLike cl2 
                    WHERE cl2.compilation.id = c.id 
                      AND cl2.user.id = :userId
                )
                THEN true ELSE false
            END as likedByUser,
        
            (SELECT COUNT(cs) 
             FROM CompilationSubscription cs 
             WHERE cs.compilation.id = c.id) as subscribersCount,
        
            CASE 
                WHEN :userId IS NULL THEN false
                WHEN EXISTS (
                    SELECT 1 
                    FROM CompilationSubscription cs2 
                    WHERE cs2.compilation.id = c.id 
                      AND cs2.user.id = :userId
                )
                THEN true ELSE false
            END as isSubscribed
        
        FROM Compilation c
        WHERE c.id = :id
    """)
    Optional<CompilationProjection> findViewById(Long id, Long userId);

    @Query("""
    SELECT 
        c.id as id,
        c.title as title,
        c.description as description,
        c.isPublic as isPublic,
        c.coverUrl as coverUrl,
    
        c.author.id as authorId,
        c.author.username as authorName,
    
        (SELECT COUNT(cl) 
         FROM CompilationLike cl 
         WHERE cl.compilation.id = c.id) as likesCount,
    
        CASE 
            WHEN :currentUserId IS NULL THEN false
            WHEN EXISTS (
                SELECT 1 
                FROM CompilationLike cl2 
                WHERE cl2.compilation.id = c.id 
                  AND cl2.user.id = :currentUserId
            )
            THEN true ELSE false
        END as likedByUser,
    
        (SELECT COUNT(cs) 
         FROM CompilationSubscription cs 
         WHERE cs.compilation.id = c.id) as subscribersCount,
    
        CASE 
            WHEN :currentUserId IS NULL THEN false
            WHEN EXISTS (
                SELECT 1 
                FROM CompilationSubscription cs2 
                WHERE cs2.compilation.id = c.id 
                  AND cs2.user.id = :currentUserId
            )
            THEN true ELSE false
        END as isSubscribed
    
    FROM Compilation c
    JOIN CompilationLike cl ON cl.compilation.id = c.id
    WHERE cl.user.id = :userId
    AND (
        c.isPublic = true OR c.author.id = :currentUserId
        )
    """)
    List<CompilationProjection> findAllLikedByUserId(Long userId, Long currentUserId);

    @Query("""
        SELECT 
            c.id as id,
            c.title as title,
            c.description as description,
            c.isPublic as isPublic,
            c.coverUrl as coverUrl,
    
            c.author.id as authorId,
            c.author.username as authorName,
    
            COUNT(cl) as likesCount,
    
            CASE 
                WHEN :currentUserId IS NULL THEN false
                WHEN SUM(CASE WHEN cl.user.id = :currentUserId THEN 1 ELSE 0 END) > 0
                THEN true ELSE false
            END as likedByUser,
    
            (SELECT COUNT(cs) 
             FROM CompilationSubscription cs 
             WHERE cs.compilation.id = c.id) as subscribersCount,
    
            false as isSubscribed
    
        FROM Compilation c
        LEFT JOIN CompilationLike cl 
            ON cl.compilation.id = c.id
            AND cl.createdAt >= :fromDate
    
        WHERE c.isPublic = true
    
        GROUP BY 
            c.id, c.title, c.description, c.isPublic, c.coverUrl,
            c.author.id, c.author.username
        HAVING COUNT(cl) > 0
    
        ORDER BY COUNT(cl) DESC
    """)
    List<CompilationProjection> findTopCompilationsLastWeek(LocalDateTime fromDate, Long currentUserId, Pageable pageable);

    @Query("""
        SELECT 
            c.id as id,
            c.title as title,
            c.description as description,
            c.isPublic as isPublic,
            c.coverUrl as coverUrl,
    
            c.author.id as authorId,
            c.author.username as authorName,
    
            COUNT(cl) as likesCount,
    
            CASE 
                WHEN :currentUserId IS NULL THEN false
                WHEN SUM(CASE WHEN cl.user.id = :currentUserId THEN 1 ELSE 0 END) > 0
                THEN true ELSE false
            END as likedByUser,
    
            (SELECT COUNT(cs) 
             FROM CompilationSubscription cs 
             WHERE cs.compilation.id = c.id) as subscribersCount,
    
            CASE 
                WHEN :currentUserId IS NULL THEN false
                WHEN EXISTS (
                    SELECT 1 
                    FROM CompilationSubscription cs2 
                    WHERE cs2.compilation.id = c.id 
                      AND cs2.user.id = :currentUserId
                )
                THEN true ELSE false
            END as isSubscribed
    
        FROM Compilation c
        LEFT JOIN CompilationLike cl ON cl.compilation.id = c.id
    
        WHERE c.isPublic = true
          AND (:excludeIds IS NULL OR c.id NOT IN :excludeIds)
    
        GROUP BY 
            c.id, c.title, c.description, c.isPublic, c.coverUrl,
            c.author.id, c.author.username
    
        ORDER BY c.createdAt DESC
    """)
    List<CompilationProjection> findLatest(List<Long> excludeIds, Long currentUserId, Pageable pageable);
}