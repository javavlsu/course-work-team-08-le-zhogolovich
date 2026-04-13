package ru.vlsu.ispi.movieproject.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = {NotReservedUsernameValidator.class})
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotReservedUsername {
    String message() default "Это имя пользователя недоступно";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
