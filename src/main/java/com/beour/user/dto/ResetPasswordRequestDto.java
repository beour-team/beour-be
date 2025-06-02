package com.beour.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequestDto {

  @NotBlank(message = "아이디는 필수입니다.")
  private String loginId;

  @NotBlank(message = "이름은 필수입니다.")
  private String name;

  @NotBlank(message = "핸드폰 번호는 필수입니다.")
  @Pattern(regexp = "^\\d{10,11}$", message = "전화번호는 숫자만 10~11자리로 입력하세요.")
  private String phone;

  @NotBlank(message = "이메일은 필수입니다.")
  @Email(message = "올바른 이메일 형식이어야 합니다.")
  private String email;

}
