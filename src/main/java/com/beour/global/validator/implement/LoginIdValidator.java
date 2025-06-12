package com.beour.global.validator.implement;

import com.beour.global.validator.annotation.ValidLoginId;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class LoginIdValidator implements ConstraintValidator<ValidLoginId, String> {
    private static final String LOGIN_ID_REGEX = "^[a-zA-Z0-9]{5,15}$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) return false;
        return value.matches(LOGIN_ID_REGEX);
    }
}
