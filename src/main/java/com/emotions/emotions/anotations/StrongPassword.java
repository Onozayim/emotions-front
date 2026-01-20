package com.emotions.emotions.anotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = StrongPasswordValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StrongPassword {
    String message() default "La contraseña debe ser de al menos 8 caracteres, y debe tener una combinación de minúsculas, mayúsculas y números";

    Class <?>[] groups() default{};
    Class<? extends Payload>[] payload() default{};
}