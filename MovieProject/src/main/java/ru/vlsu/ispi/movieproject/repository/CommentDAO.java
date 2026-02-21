package ru.vlsu.ispi.movieproject.repository;

import ru.vlsu.ispi.movieproject.model.Comment;

import java.util.List;
import java.util.Optional;

/**
 * DAO-интерфейс для работы с сущностью {@link Comment}.
 *
 * <p>
 * Определяет CRUD-операции для управления комментариями пользователей.
 * </p>
 */
public interface CommentDAO {

    /**
     * Сохраняет новый комментарий в базе данных.
     *
     * @param comment объект комментария
     * @return сохранённый комментарий со сгенерированным идентификатором
     */
    Comment create(Comment comment);

    /**
     * Находит комментарий по идентификатору.
     *
     * @param id идентификатор комментария
     * @return Optional с найденным комментарием
     *         или Optional.empty(), если комментарий не найден
     */
    Optional<Comment> findById(Long id);

    /**
     * Возвращает список всех комментариев.
     *
     * @return список комментариев
     */
    List<Comment> findAll();

    /**
     * Обновляет существующий комментарий.
     *
     * @param comment комментарий с обновлёнными данными
     * @return обновлённый комментарий
     */
    Comment update(Comment comment);

    /**
     * Удаляет комментарий по идентификатору.
     *
     * @param id идентификатор комментария
     */
    void deleteById(Long id);

    /**
     * Проверяет существование комментария по идентификатору.
     *
     * @param id идентификатор комментария
     * @return true — если комментарий существует, иначе false
     */
    boolean existsById(Long id);

    /**
     * Возвращает все комментарии конкретного фильма.
     *
     * @param movieId идентификатор фильма
     * @return список комментариев фильма
     */
    List<Comment> findByMovieId(Long movieId);

    /**
     * Возвращает все комментарии, оставленные пользователем.
     *
     * @param userId идентификатор пользователя
     * @return список комментариев пользователя
     */
    List<Comment> findByUserId(Long userId);
}
