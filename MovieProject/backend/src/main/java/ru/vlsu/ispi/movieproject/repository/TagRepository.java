package ru.vlsu.ispi.movieproject.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.vlsu.ispi.movieproject.dto.tag.TagDto;
import ru.vlsu.ispi.movieproject.model.Tag;
import ru.vlsu.ispi.movieproject.enums.TagType;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с сущностью {@link Tag}.
 *
 * <p>Предоставляет стандартные CRUD-операции,
 * а также методы поиска тегов по имени и типу</p>
 *
 * <p>Реализуется автоматически Spring Data JPA</p>
 *
 * @author
 * @since 1.0
 */
public interface TagRepository extends JpaRepository<Tag, Long> {

    /**
     * Находит тег по его уникальному имени
     *
     * @param name название тега
     * @return Optional с найденным тегом или пустой Optional, если тег не существует
     */
    Optional<Tag> findByName(String name);

    /**
     * Возвращает список тегов указанного типа
     *
     * @param type тип тега (GENRE, MOOD и т.д.)
     * @return список тегов данного типа
     */
    List<Tag> findByType(TagType type);

    /**
     * Проверяет существование тега с указанным именем
     *
     * @param name название тега
     * @return true — если тег существует, иначе false
     */
    boolean existsByName(String name);

    Page<Tag> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Tag> findAllByOrderByNameAsc(Pageable pageable);
}