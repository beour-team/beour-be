package com.beour.user.controller;

import com.beour.global.exception.exceptionType.UserNotFoundException;
import com.beour.global.response.ApiResponse;
import com.beour.user.dto.FindLoginIdRequestDto;
import com.beour.user.dto.FindLoginIdResponseDto;
import com.beour.user.dto.ResetPasswordRequestDto;
import com.beour.user.dto.ResetPasswordResponseDto;
import com.beour.user.service.LoginService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class LoginController {

  private final LoginService loginService;

  @PostMapping("/api/users/find-login-id")
  public ApiResponse<FindLoginIdResponseDto> findLoginId(
      @Valid @RequestBody FindLoginIdRequestDto dto) {

    return loginService.findLoginId(dto);
  }

  @PostMapping("/api/users/reset-pw")
  public ApiResponse<ResetPasswordResponseDto> resetPassword(
      @Valid @RequestBody ResetPasswordRequestDto dto) {
    return loginService.resetPassword(dto);
  }

}
