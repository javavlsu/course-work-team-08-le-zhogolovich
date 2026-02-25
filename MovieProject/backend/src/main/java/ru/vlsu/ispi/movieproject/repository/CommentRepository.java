package ru.vlsu.ispi.movieproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vlsu.ispi.movieproject.model.Comment;

import java.util.List;

/**
 * Репозиторий для работы с сущностью {@link Comment}.
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
 *     <li>save(Comment) — создание и обновление комментария</li>
 *     <li>findById(Long) — поиск по идентификатору</li>
 *     <li>findAll() — получение всех комментариев</li>
 *     <li>deleteById(Long) — удаление комментария</li>
 *     <li>existsById(Long) — проверка существования</li>
 * </ul>
 * </p>
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * Возвращает все комментарии конкретного фильма
     *
     * @param movieId идентификатор фильма
     * @return список комментариев фильма
     */
    List<Comment> findByMovieId(Long movieId);
}