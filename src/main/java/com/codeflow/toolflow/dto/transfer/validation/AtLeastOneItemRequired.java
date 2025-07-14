package com.codeflow.toolflow.dto.transfer.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validation annotation to ensure that at least one list of items (tools, parts, or vehicles)
 * is present in a transfer request.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AtLeastOneItemValidator.class)
public @interface AtLeastOneItemRequired {
    String message() default "At least one item (tool, vehicle part, or vehicle) must be provided for the transfer.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
