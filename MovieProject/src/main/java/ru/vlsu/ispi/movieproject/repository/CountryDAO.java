package ru.vlsu.ispi.movieproject.repository;

import ru.vlsu.ispi.movieproject.model.Country;

import java.util.List;
import java.util.Optional;

/**
 * DAO-интерфейс для работы с сущностью {@link Country}.
 *
 * <p>
 * Предоставляет методы доступа к данным стран производства фильмов.
 * Интерфейс включает базовые CRUD-операции, а также методы поиска
 * по уникальным полям.
 * </p>
 */
public interface CountryDAO {

    /**
     * Сохраняет новую страну в базе данных.
     *
     * @param country объект страны
     * @return сохранённая страна с присвоенным идентификатором
     */
    Country create(Country country);

    /**
     * Находит страну по идентификатору.
     *
     * @param id идентификатор страны
     * @return Optional с найденной страной
     *         или Optional.empty(), если страна не найдена
     */
    Optional<Country> findById(Long id);

    /**
     * Возвращает список всех стран.
     *
     * @return список стран
     */
    List<Country> findAll();

    /**
     * Обновляет существующую страну.
     *
     * @param country объект страны с обновлёнными данными
     * @return обновлённая страна
     */
    Country update(Country country);

    /**
     * Удаляет страну по идентификатору.
     *
     * @param id идентификатор страны
     */
    void deleteById(Long id);

    /**
     * Проверяет существование страны по идентификатору.
     *
     * @param id идентификатор страны
     * @return true — если страна существует, иначе false
     */
    boolean existsById(Long id);

    /**
     * Выполняет поиск страны по названию.
     *
     * <p>
     * Поле name является уникальным в базе данных.
     * </p>
     *
     * @param name название страны
     * @return Optional с найденной страной
     */
    Optional<Country> findByName(String name);

    /**
     * Находит страну по идентификатору Kinopoisk.
     *
     * @param kinopoiskId идентификатор страны во внешнем API
     * @return Optional с найденной страной
     */
    Optional<Country> findByKinopoiskId(Integer kinopoiskId);
}