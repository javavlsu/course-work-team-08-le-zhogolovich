package ru.vlsu.ispi.movieproject.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.vlsu.ispi.movieproject.model.Compilation;
import ru.vlsu.ispi.movieproject.projection.CompilationProjection;
import ru.vlsu.ispi.movieproject.projection.CompilationStatsProjection;

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
        COUNT(cl.id) as likesCount,
        CASE 
            WHEN :userId IS NULL THEN false
            WHEN SUM(CASE WHEN cl.user.id = :userId THEN 1 ELSE 0 END) > 0 
            THEN true 
            ELSE false 
        END as likedByUser
    
    FROM Compilation c
    LEFT JOIN CompilationLike cl ON cl.compilation.id = c.id
    
    WHERE c.id = :id
    """)
    CompilationStatsProjection getStats(Long id, Long userId);

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
            THEN true 
            ELSE false 
        END as likedByUser
    
    FROM Compilation c
    WHERE c.isPublic = true
    """)
    Page<CompilationProjection> findAllWithLikes(Pageable pageable, Long userId);
}