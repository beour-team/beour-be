package com.beour.global.exception;

import com.beour.global.exception.exceptionType.InputInvalidFormatException;
import com.beour.global.response.ErrorResponse;
import com.beour.space.host.enums.SpaceCategory;
import java.util.Arrays;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(
      MethodArgumentNotValidException ex) {
    FieldError fieldError = ex.getBindingResult().getFieldErrors().get(0);
    String message = fieldError.getDefaultMessage();

    return ResponseEntity.badRequest()
        .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "VALIDATION_ERROR", message));
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleEnumBindingException(MethodArgumentTypeMismatchException ex) {
    return ResponseEntity.badRequest()
        .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "INVALID_ENUM", "유효하지 않은 값입니다"));
  }

  @ExceptionHandler(InputInvalidFormatException.class)
  public ResponseEntity<ErrorResponse> handleInputFormat(InputInvalidFormatException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "INPUT_INVALID_FORMAT", ex.getMessage()));
  }

}
