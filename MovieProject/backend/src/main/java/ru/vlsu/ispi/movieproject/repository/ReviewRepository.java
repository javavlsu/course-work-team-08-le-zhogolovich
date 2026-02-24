package ru.vlsu.ispi.movieproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vlsu.ispi.movieproject.model.Review;
import ru.vlsu.ispi.movieproject.model.ReviewStatus;

import java.util.List;

/**
 * Репозиторий для работы с сущностью {@link Review}.
 *
 * <p>
 * Представляет пользовательские рецензии на фильмы
 * Предоставляет стандартные CRUD-операции,
 * автоматически реализуемые Spring Data JPA
 * через интерфейс {@link JpaRepository}.
 * </p>
 *
 * <p>
 * Доступные операции (реализуются автоматически):
 * <ul>
 *     <li>save(Review) — создание и обновление рецензии</li>
 *     <li>findById(Long) — поиск по идентификатору</li>
 *     <li>findAll() — получение всех рецензий</li>
 *     <li>deleteById(Long) — удаление рецензии</li>
 *     <li>existsById(Long) — проверка существования</li>
 * </ul>
 * </p>
 *
 *  * <p>
 *  * Дополнительно содержит методы поиска по внешним идентификаторам
 *  * </p>
 */
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * Возвращает все рецензии для указанного фильма.
     *
     * @param movieId идентификатор фильма
     * @return список рецензий фильма
     */
    List<Review> findByMovieId(Long movieId);

    /**
     * Возвращает все рецензии, созданные пользователем.
     *
     * @param authorId идентификатор пользователя
     * @return список рецензий пользователя
     */
    List<Review> findByAuthorId(Long authorId);

}