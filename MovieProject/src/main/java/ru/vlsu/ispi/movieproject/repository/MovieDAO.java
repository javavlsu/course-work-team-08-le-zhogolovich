package ru.vlsu.ispi.movieproject.repository;

import ru.vlsu.ispi.movieproject.model.Movie;

import java.util.List;
import java.util.Optional;

/**
 * DAO-интерфейс для работы с сущностью {@link Movie}.
 *
 * <p>
 * Предоставляет методы доступа к данным фильмов.
 * Интерфейс включает базовые CRUD-операции, а также методы поиска фильмов по различным параметрам
 * (название, год выпуска, внешний идентификатор и др.)
 * </p>
 */
public interface MovieDAO {

    /**
     * Сохраняет новый фильм в базе данных.
     *
     * @param movie объект фильма
     * @return сохранённый фильм с присвоенным идентификатором
     */
    Movie create(Movie movie);

    /**
     * Выполняет поиск фильма по идентификатору.
     *
     * @param id идентификатор фильма
     * @return Optional с найденным фильмом или Optional.empty(), если фильм не найден
     */
    Optional<Movie> findById(Long id);

    /**
     * Возвращает список всех фильмов.
     *
     * @return список фильмов
     */
    List<Movie> findAll();

    /**
     * Обновляет данные существующего фильма.
     *
     * @param movie фильм с обновлёнными данными
     * @return обновлённый фильм
     */
    Movie update(Movie movie);

    /**
     * Удаляет фильм по идентификатору.
     *
     * @param id идентификатор фильма
     */
    void deleteById(Long id);

    /**
     * Проверяет существование фильма в базе данных.
     *
     * @param id идентификатор фильма
     * @return true — если фильм существует, иначе false
     */
    boolean existsById(Long id);

    /**
     * Находит фильм по идентификатору Kinopoisk.
     *
     * @param kinopoiskId внешний идентификатор фильма
     * @return Optional с найденным фильмом
     */
    Optional<Movie> findByKinopoiskId(Integer kinopoiskId);

    /**
     * Выполняет поиск фильмов по названию (частичное совпадение).
     *
     * @param name название фильма
     * @return список найденных фильмов
     */
    List<Movie> findByNameContaining(String name);

    /**
     * Возвращает фильмы, выпущенные в указанном году.
     *
     * @param releaseYear год выпуска
     * @return список фильмов
     */
    List<Movie> findByReleaseYear(Integer releaseYear);

    /**
     * Возвращает фильмы указанного жанра.
     *
     * @param genreId идентификатор жанра
     * @return список фильмов данного жанра
     */
    List<Movie> findByGenreId(Long genreId);

    /**
     * Возвращает фильмы указанной страны производства.
     *
     * @param countryId идентификатор страны
     * @return список фильмов
     */
    List<Movie> findByCountryId(Long countryId);
}