package ru.vlsu.ispi.movieproject.repository;

import ru.vlsu.ispi.movieproject.model.Genre;

import java.util.List;
import java.util.Optional;

/**
 * DAO-интерфейс для работы с сущностью {@link Genre}.
 *
 * <p>
 * Определяет методы доступа к данным жанров фильмов.
 * Включает базовые CRUD-операции, а также методы поиска по уникальным и внешним идентификаторам.
 * </p>
 *
\
 */
public interface GenreDAO {

    /**
     * Сохраняет новый жанр в базе данных.
     *
     * @param genre объект жанра
     * @return сохранённый жанр с присвоенным идентификатором
     */
    Genre create(Genre genre);

    /**
     * Находит жанр по идентификатору.
     *
     * @param id идентификатор жанра
     * @return Optional с найденным жанр или Optional.empty(), если жанр не найден
     */
    Optional<Genre> findById(Long id);

    /**
     * Возвращает список всех жанров.
     *
     * @return список жанров
     */
    List<Genre> findAll();

    /**
     * Обновляет существующий жанр.
     *
     * @param genre объект жанра с обновлёнными данными
     * @return обновлённый жанр
     */
    Genre update(Genre genre);

    /**
     * Удаляет жанр по идентификатору.
     *
     * @param id идентификатор жанра
     */
    void deleteById(Long id);

    /**
     * Проверяет существование жанра по идентификатору.
     *
     * @param id идентификатор жанра
     * @return true — если жанр существует, иначе false
     */
    boolean existsById(Long id);

    /**
     * Выполняет поиск жанра по названию.
     *
     * <p>
     * Поле name является уникальным в базе данных.
     * </p>
     *
     * @param name название жанра
     * @return Optional с найденным жанром
     */
    Optional<Genre> findByName(String name);

    /**
     * Находит жанр по идентификатору Kinopoisk.
     *
     * @param kinopoiskId внешний идентификатор жанра
     * @return Optional с найденным жанром
     */
    Optional<Genre> findByKinopoiskId(Integer kinopoiskId);
}