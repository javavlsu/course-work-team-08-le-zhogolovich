package ru.vlsu.ispi.movieproject.repository;
import ru.vlsu.ispi.movieproject.model.Collection;

import java.util.List;
import java.util.Optional;

/**
 * DAO-интерфейс для работы с сущностью {@link Collection}.
 *
 * <p>
 * Определяет базовые CRUD-операции для управления коллекциями фильмов:
 * создание, получение, обновление и удаление коллекций.
 * </p>
 */
public interface CollectionDAO {

    /**
     * Сохраняет новую коллекцию в базе данных.
     *
     * @param collection объект коллекции для сохранения
     * @return сохранённая коллекция со сгенерированным идентификатором
     */
    Collection create(Collection collection);

    /**
     * Возвращает коллекцию по её идентификатору.
     *
     * @param id идентификатор коллекции
     * @return Optional с коллекцией, если она найдена,
     *         иначе Optional.empty()
     */
    Optional<Collection> findById(Long id);

    /**
     * Возвращает список всех коллекций.
     *
     * @return список всех коллекций из базы данных
     */
    List<Collection> findAll();

    /**
     * Обновляет существующую коллекцию.
     *
     * @param collection объект коллекции с обновлёнными данными
     * @return обновлённая коллекция
     */
    Collection update(Collection collection);

    /**
     * Удаляет коллекцию по идентификатору.
     *
     * @param id идентификатор удаляемой коллекции
     */
    void deleteById(Long id);

    /**
     * Проверяет существование коллекции по идентификатору.
     *
     * @param id идентификатор коллекции
     * @return true — если коллекция существует, иначе false
     */
    boolean existsById(Long id);
}