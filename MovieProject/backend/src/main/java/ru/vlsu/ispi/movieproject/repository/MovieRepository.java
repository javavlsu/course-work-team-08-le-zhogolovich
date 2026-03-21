package ru.vlsu.ispi.movieproject.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import ru.vlsu.ispi.movieproject.model.Movie;

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
public interface MovieRepository extends JpaRepository<Movie, Long> {
    boolean existsByKinopoiskId(Integer kinopoiskId);

    @Query("SELECT m.kinopoiskId from Movie m")
    Set<Integer> findAllKinopoiskId();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from Movie m where m.id = :id")
    Optional<Movie> findByIdForUpdate(Long id);
}
