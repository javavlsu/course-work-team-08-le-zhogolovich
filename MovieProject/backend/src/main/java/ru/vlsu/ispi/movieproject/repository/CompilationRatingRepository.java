package ru.vlsu.ispi.movieproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vlsu.ispi.movieproject.model.CollectionRating;
import ru.vlsu.ispi.movieproject.model.CompilationRatingId;

/**
 * Репозиторий для работы с сущностью {@link CollectionRating}.
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
public interface CompilationRatingRepository
        extends JpaRepository<CollectionRating, CompilationRatingId> {
}