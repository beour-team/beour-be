package com.beour.user.dto;

import com.beour.global.validator.annotation.ValidLoginId;
import com.beour.global.validator.annotation.ValidNickname;
import com.beour.global.validator.annotation.ValidPhoneNum;
import com.beour.global.validator.annotation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupRequestDto {

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @ValidNickname
    private String nickname;

    @Pattern(regexp = "^(HOST|GUEST)$", message = "역할은 HOST 또는 GUEST만 가능합니다.")
    private String role;

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이어야 합니다.")
    private String email;

    @ValidLoginId
    private String loginId;

    @ValidPassword
    private String password;

    @ValidPhoneNum
    private String phone;

    public void encodingPassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    @Builder
    public SignupRequestDto(String name, String nickname, String role, String email, String loginId,
        String password, String phone) {
        this.name = name;
        this.nickname = nickname;
        this.role = role;
        this.email = email;
        this.loginId = loginId;
        this.password = password;
        this.phone = phone;

    }
}
