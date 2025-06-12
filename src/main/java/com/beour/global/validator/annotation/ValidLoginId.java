package com.beour.global.validator.annotation;

import com.beour.global.validator.implement.LoginIdValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 사용자의 로그인 아이디 유효성을 검사하는 어노테이션입니다.
 */
@Documented
@Constraint(validatedBy = LoginIdValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidLoginId {
    String message() default "아이디는 5~15자의 영어 대소문자와 숫자만 사용 가능합니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
