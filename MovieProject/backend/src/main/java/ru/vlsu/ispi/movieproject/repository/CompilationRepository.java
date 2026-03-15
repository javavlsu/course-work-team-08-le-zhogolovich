package ru.vlsu.ispi.movieproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vlsu.ispi.movieproject.model.Compilation;

/**
 * Репозиторий для работы с сущностью {@link Compilation}.
 *
 * <p>
 * Предоставляет стандартные CRUD-операции, автоматически реализуемые Spring Data JPA
 * через интерфейс {@link JpaRepository}
 * </p>
 *
 * <p>
 * Доступные операции (реализуются автоматически):
 * <ul>
 *     <li>save(Collection) — создание и обновление коллекции</li>
 *     <li>findById(Long) — поиск по идентификатору</li>
 *     <li>findAll() — получение всех коллекций</li>
 *     <li>deleteById(Long) — удаление коллекции</li>
 *     <li>existsById(Long) — проверка существования</li>
 * </ul>
 * </p>
 *
 * <p>
 * Дополнительно содержит методы поиска коллекций,
 * автоматически генерируемые Spring Data JPA
 * на основе имени метода.
 * </p>
 */
public interface CompilationRepository extends JpaRepository<Compilation, Long> {

}