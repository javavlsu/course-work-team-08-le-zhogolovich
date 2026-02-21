package ru.vlsu.ispi.movieproject.repository;

import ru.vlsu.ispi.movieproject.model.CollectionRating;
import ru.vlsu.ispi.movieproject.model.CollectionRatingId;

import java.util.List;
import java.util.Optional;

/**
 * DAO-интерфейс для работы с сущностью {@link CollectionRating}.
 *
 * <p>
 * Сущность представляет оценку коллекции пользователем
 * </p>
 *
 * <p>
 * Интерфейс определяет CRUD-операции и методы доступа к оценкам коллекций пользователей
 * </p>
 */
public interface CollectionRatingDAO {

    /**
     * Сохраняет новую оценку коллекции.
     *
     * @param rating объект оценки
     * @return сохранённая оценка
     */
    CollectionRating create(CollectionRating rating);

    /**
     * Находит оценку по составному идентификатору.
     *
     * @param id составной идентификатор (userId + collectionId)
     * @return Optional с найденной оценкой или Optional.empty()
     */
    Optional<CollectionRating> findById(CollectionRatingId id);

    /**
     * Возвращает все оценки коллекций.
     *
     * @return список всех оценок
     */
    List<CollectionRating> findAll();

    /**
     * Обновляет существующую оценку.
     *
     * @param rating объект оценки с обновлёнными данными
     * @return обновлённая оценка
     */
    CollectionRating update(CollectionRating rating);

    /**
     * Удаляет оценку по составному идентификатору.
     *
     * @param id составной идентификатор оценки
     */
    void deleteById(CollectionRatingId id);

    /**
     * Проверяет существование оценки по идентификатору.
     *
     * @param id составной идентификатор
     * @return true — если оценка существует, иначе false
     */
    boolean existsById(CollectionRatingId id);

    /**
     * Возвращает все оценки конкретной коллекции.
     *
     * @param collectionId идентификатор коллекции
     * @return список оценок данной коллекции
     */
    List<CollectionRating> findByCollectionId(Long collectionId);

    /**
     * Возвращает все оценки, поставленные пользователем.
     *
     * @param userId идентификатор пользователя
     * @return список оценок пользователя
     */
    List<CollectionRating> findByUserId(Long userId);
}