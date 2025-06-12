package com.beour.global.validator.implement;

import com.beour.global.validator.annotation.ValidNickname;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NicknameValidator implements ConstraintValidator<ValidNickname, String> {
    private static final String NICKNAME_REGEX = "^.{1,8}$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) return false;
        return value.matches(NICKNAME_REGEX);
    }
}
