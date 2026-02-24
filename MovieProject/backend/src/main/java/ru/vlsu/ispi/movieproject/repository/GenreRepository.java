package ru.vlsu.ispi.movieproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vlsu.ispi.movieproject.model.Genre;

import java.util.Optional;

/**
 * Репозиторий для работы с сущностью {@link Genre}.
 *
 * <p>
 * Предоставляет стандартные CRUD-операции,
 * автоматически реализуемые Spring Data JPA
 * через интерфейс {@link JpaRepository}.
 * </p>
 *
 * <p>
 * Доступные операции (реализуются автоматически):
 * <ul>
 *     <li>save(Genre) — создание и обновление жанра</li>
 *     <li>findById(Long) — поиск по идентификатору</li>
 *     <li>findAll() — получение списка жанров</li>
 *     <li>deleteById(Long) — удаление жанра</li>
 *     <li>existsById(Long) — проверка существования</li>
 * </ul>
 * </p>
 *
 * <p>
 * Дополнительно содержит методы поиска по  внешним идентификаторам.
 * </p>
 */
public interface GenreRepository extends JpaRepository<Genre, Long> {
    /**
     * Находит жанр по идентификатору Kinopoisk
     *
     * @param kinopoiskId внешний идентификатор жанра
     * @return Optional с найденным жанром
     */
    Optional<Genre> findByKinopoiskId(Integer kinopoiskId);
}