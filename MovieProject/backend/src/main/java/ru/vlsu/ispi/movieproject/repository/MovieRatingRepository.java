package ru.vlsu.ispi.movieproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.vlsu.ispi.movieproject.model.MovieRating;
import ru.vlsu.ispi.movieproject.model.MovieRatingId;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с сущностью {@link MovieRating}
 *
 * <p>
 * Сущность представляет оценку фильма пользователем
 * Использует составной первичный ключ {@link MovieRatingId},
 * включающий идентификаторы пользователя и фильма
 * </p>
 *
 * <p>
 * Предоставляет стандартные CRUD-операции,
 * автоматически реализуемые Spring Data JPA
 * через интерфейс {@link JpaRepository}
 * </p>
 *
 * <p>
 * Доступные операции (реализуются автоматически):
 * <ul>
 *     <li>save(MovieRating) — создание и обновление оценки</li>
 *     <li>findById(MovieRatingId) — поиск по составному ключу</li>
 *     <li>findAll() — получение всех оценок</li>
 *     <li>deleteById(MovieRatingId) — удаление оценки</li>
 *     <li>existsById(MovieRatingId) — проверка существования</li>
 * </ul>
 * </p>
 *  * <p>
 *  * Дополнительно содержит методы поиска по внешним идентификаторам
 *  * </p>
 */
public interface MovieRatingRepository extends JpaRepository<MovieRating, MovieRatingId> {
    @Query("""
        SELECT AVG(r.rating)
        FROM MovieRating r
        WHERE r.movie.id = :movieId
    """)
    Double getAverageRating(Long movieId);

    @Query("""
        SELECT COUNT(r)
        FROM MovieRating r
        WHERE r.movie.id = :movieId
    """)
    Integer getRatingsCount(Long movieId);

    @Query("""
        SELECT r.rating
        FROM MovieRating r
        WHERE r.movie.id = :movieId AND r.user.id = :userId
    """)
    Optional<Integer> getUserRating(Long movieId, Long userId);
}