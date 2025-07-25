package com.beour.user.controller;

import com.beour.global.jwt.ManageCookie;
import com.beour.global.response.ApiResponse;
import com.beour.user.dto.FindLoginIdRequestDto;
import com.beour.user.dto.FindLoginIdResponseDto;
import com.beour.user.dto.ReissueAccesstokenResponseDto;
import com.beour.user.dto.ResetPasswordRequestDto;
import com.beour.user.dto.ResetPasswordResponseDto;
import com.beour.user.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/api/users/find/login-id")
    public ApiResponse<FindLoginIdResponseDto> findLoginId(
        @Valid @RequestBody FindLoginIdRequestDto dto) {
        return ApiResponse.ok(loginService.findLoginId(dto));
    }

    @PostMapping("/api/users/reset/password")
    public ApiResponse<ResetPasswordResponseDto> resetPassword(
        @Valid @RequestBody ResetPasswordRequestDto dto) {
        return ApiResponse.ok(loginService.resetPassword(dto));
    }

    @PostMapping("/api/token/reissue")
    public ApiResponse<ReissueAccesstokenResponseDto> reissue(HttpServletRequest request,
        HttpServletResponse response) {
        String[] tokens = loginService.reissueRefreshToken(request);
        String newAccessToken = tokens[0];
        String newRefreshToken = tokens[1];
        response.setHeader("Authorization", newAccessToken);
//      response.addCookie(ManageCookie.createCookie("refresh", newRefreshToken));
        boolean isSecure = request.isSecure();
        ManageCookie.addRefreshCookie(response, "refresh", newRefreshToken, isSecure);

        ReissueAccesstokenResponseDto responseDto = new ReissueAccesstokenResponseDto(
            newAccessToken);
        return ApiResponse.ok(responseDto);
    }

}
