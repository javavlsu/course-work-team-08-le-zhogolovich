package ru.vlsu.ispi.movieproject.util;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import ru.vlsu.ispi.movieproject.service.CurrentUserService;

@Component
@RequiredArgsConstructor
public class AccessUtil {
    private final CurrentUserService currentUserService;

    public void checkOwnerOrAdmin(Long ownerId) {
        if (ownerId == null) {
            throw new AccessDeniedException("Нет владельца");
        }

        Long currentUserId = currentUserService.getCurrentUserID();

        if (!ownerId.equals(currentUserId) && !currentUserService.isAdmin()) {
            throw new AccessDeniedException("Нет доступа");
        }
    }

    public void checkAdmin() {
        if (!currentUserService.isAdmin()) {
            throw new AccessDeniedException("Требуются права администратора");
        }
    }
}
