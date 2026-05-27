package ru.vlsu.ispi.movieproject.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import ru.vlsu.ispi.movieproject.model.Movie;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Репозиторий для работы с сущностью Movie
 *
 * <p>
 * Предоставляет стандартные CRUD-операции,
 * автоматически реализуемые Spring Data JPA
 * через интерфейс {@link JpaRepository}
 * </p>
 *
 * <p>
 * Включает операции:
 * <ul>
 *     <li>сохранение фильма</li>
 *     <li>поиск по id</li>
 *     <li>получение списка фильмов</li>
 *     <li>обновление</li>
 *     <li>удаление</li>
 * </ul>
 * </p>
 */
public interface MovieRepository extends JpaRepository<Movie, Long>, JpaSpecificationExecutor<Movie> {
    @EntityGraph(attributePaths = {"genres", "countries", "tags","externalSources"})
    Optional<Movie> findById(Long id);

    @Query("SELECT m.kinopoiskId from Movie m")
    Set<Integer> findAllKinopoiskId();

    @Query("select m.id from Movie m")
    List<Long> findAllIds();

    @Query("""
    SELECT m
    FROM Movie m
        LEFT JOIN MovieRating mr 
            ON mr.movie.id = m.id
            AND mr.createdAt >= :fromDate
    
        GROUP BY m.id
        HAVING AVG(mr.rating) >= :minRating
    
        ORDER BY COUNT(mr) DESC, AVG(mr.rating) DESC
    """)
    List<Movie> findTopMoviesLastWeek(LocalDateTime fromDate, Double minRating, Pageable pageable);

    @Query("""
        SELECT m
        FROM Movie m
        WHERE (:excludeIds IS NULL OR m.id NOT IN :excludeIds)
        ORDER BY m.detailsLoadedAt DESC
    """)
    List<Movie> findLatest(List<Long> excludeIds, Pageable pageable);

    @Query("""
        SELECT DISTINCT m
        FROM Movie m
        WHERE m.id IN :ids
    """)
    List<Movie> findAllWithRelations(List<Long> ids);
}
