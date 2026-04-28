package ru.vlsu.ispi.movieproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.vlsu.ispi.movieproject.model.CompilationLike;
import ru.vlsu.ispi.movieproject.model.CompilationLikeId;
import ru.vlsu.ispi.movieproject.projection.CompilationProjection;

import java.util.Optional;

/**
 * Репозиторий для работы с сущностью {@link CompilationLike}.
 *
 * <p>
 * Сущность представляет оценку коллекции пользователем
 * Репозиторий предоставляет стандартные CRUD-операции,
 * автоматически реализуемые Spring Data JPA через
 * интерфейс {@link JpaRepository}.
 * </p>
 *
 * <p>
 * Доступные операции (реализуются автоматически):
 * <ul>
 *     <li>save(CollectionRating) — создание и обновление оценки</li>
 *     <li>findById(CollectionRatingId) — поиск по составному ключу</li>
 *     <li>findAll() — получение всех оценок</li>
 *     <li>deleteById(CollectionRatingId) — удаление оценки</li>
 *     <li>existsById(CollectionRatingId) — проверка существования</li>
 * </ul>
 * </p>
 */
public interface CompilationLikeRepository extends JpaRepository<CompilationLike, CompilationLikeId> {
    @Query("""
        SELECT COUNT(cl)
        FROM CompilationLike cl
        WHERE cl.compilation.id = :id
    """)
    long countLikes(Long id);
    boolean existsByUserIdAndCompilationId(Long userId, Long compilationId);
    void deleteByUserIdAndCompilationId(Long userId, Long compilationId);

    @Query("""
    SELECT 
        c.id as id,
        c.title as title,
        c.description as description,
        c.isPublic as isPublic,
        c.coverUrl as coverUrl,
        c.author.id as authorId,
        c.author.username as authorName,
    
        COUNT(cl.id) as likesCount,
    
        CASE 
            WHEN SUM(CASE WHEN cl.user.id = :userId THEN 1 ELSE 0 END) > 0 
            THEN true 
            ELSE false 
        END as likedByUser
    
    FROM Compilation c
    LEFT JOIN CompilationLike cl ON cl.compilation.id = c.id
    
    WHERE c.id = :id
    GROUP BY c.id, c.title, c.description, c.isPublic, c.coverUrl, c.author.id, c.author.username
    """)
    Optional<CompilationProjection> findFullById(Long id, Long userId);
}