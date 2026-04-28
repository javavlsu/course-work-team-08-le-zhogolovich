package ru.vlsu.ispi.movieproject.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.vlsu.ispi.movieproject.model.Compilation;
import ru.vlsu.ispi.movieproject.projection.CompilationProjection;

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
    """)
    Optional<Compilation> findByIdWithMovies(Long id);

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
}