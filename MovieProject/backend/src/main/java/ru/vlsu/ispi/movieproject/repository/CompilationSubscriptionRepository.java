package ru.vlsu.ispi.movieproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vlsu.ispi.movieproject.model.CompilationSubscription;

import java.util.Optional;

public interface CompilationSubscriptionRepository extends JpaRepository<CompilationSubscription, Long> {
    Optional<CompilationSubscription> findByUserIdAndCompilationId(Long userId, Long compilationId);
    boolean existsByUserIdAndCompilationId(Long userId, Long compilationId);
}
