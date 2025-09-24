package com.beour.global.exception.exceptionType;

import com.beour.global.exception.error.ErrorCode;
import lombok.Getter;

@Getter
public class InvalidCategoryException extends RuntimeException {
    private final int errorCode;
    private final String codeName;

    public InvalidCategoryException(ErrorCode errorCode, String codeName) {
        super(errorCode.getMessage());
        this.errorCode = errorCode.getCode();
        this.codeName = codeName;
    }
}
