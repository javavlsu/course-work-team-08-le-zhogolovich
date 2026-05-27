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
    boolean existsByUserIdAndCompilationId(Long userId, Long compilationId);
    void deleteByUserIdAndCompilationId(Long userId, Long compilationId);
}