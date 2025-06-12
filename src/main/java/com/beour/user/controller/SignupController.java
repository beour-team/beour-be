package com.beour.user.controller;

import com.beour.global.response.ApiResponse;
import com.beour.global.validator.annotation.ValidLoginId;
import com.beour.global.validator.annotation.ValidNickname;
import com.beour.user.dto.SignupRequestDto;
import com.beour.user.service.SignupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RequiredArgsConstructor
@RequestMapping("/api/users")
@RestController
public class SignupController {

    private final SignupService signupService;

    @PostMapping("/signup")
    public ApiResponse<String> signup(@Valid @RequestBody SignupRequestDto signUpRequestDto) {
        signupService.create(signUpRequestDto);
        return ApiResponse.ok("회원가입 완료");
    }

    @GetMapping("/signup/check/loginId")
    public ApiResponse<String> checkDuplicateLoginId(@ValidLoginId @RequestParam("loginId") String loginId) {
        signupService.checkLoginIdDuplicate(loginId);
        return ApiResponse.ok("사용 가능한 아이디 입니다.");
    }

    @GetMapping("/signup/check/nickname")
    public ApiResponse<String> checkDuplicateNickName(@ValidNickname @RequestParam("nickname") String nickname) {
        signupService.checkNicknameDuplicate(nickname);
        return ApiResponse.ok("사용 가능한 닉네임입니다.");
    }
}
