package ru.vlsu.ispi.movieproject.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

public class NotReservedUsernameValidator implements ConstraintValidator<NotReservedUsername, String> {

    private static final Set<String> RESERVED_USERNAMES = Set.of("me", "admin", "root", "api");

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null) return true;

        return !RESERVED_USERNAMES.contains(s);
    }
}
