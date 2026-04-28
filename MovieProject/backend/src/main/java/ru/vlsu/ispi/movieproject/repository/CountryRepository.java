package ru.vlsu.ispi.movieproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vlsu.ispi.movieproject.model.Country;

import java.util.Optional;

/**
 * Репозиторий для работы с сущностью {@link Country}
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
 *     <li>save(Country) — создание и обновление страны</li>
 *     <li>findById(Long) — поиск по идентификатору</li>
 *     <li>findAll() — получение списка стран</li>
 *     <li>deleteById(Long) — удаление страны</li>
 *     <li>existsById(Long) — проверка существования</li>
 * </ul>
 * </p>
 *
 * <p>
 * Дополнительно содержит методы поиска по уникальным полям
 * </p>
 */
public interface CountryRepository extends JpaRepository<Country, Long> {
    /**
     * Находит страну по идентификатору Kinopoisk.
     *
     * @param name название страны из внешнего API
     * @return Optional с найденной страной
     */
    Optional<Country> findByName(String name);
}