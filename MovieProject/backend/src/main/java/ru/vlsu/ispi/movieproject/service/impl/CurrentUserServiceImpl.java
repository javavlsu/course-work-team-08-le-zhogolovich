package ru.vlsu.ispi.movieproject.service.impl;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.vlsu.ispi.movieproject.security.CustomUserDetails;
import ru.vlsu.ispi.movieproject.service.CurrentUserService;

@Component
public class CurrentUserServiceImpl implements CurrentUserService {
    @Override
    public Long getCurrentUserID() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null) {
            return null;
        }
        if (auth instanceof AnonymousAuthenticationToken) {
            return null;
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof CustomUserDetails userDetails) {
            return userDetails.getId();
        }

        return null;
    }
}
