package com.beour.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginDto {

  @NotBlank(message = "아이디는 필수입니다.")
  private String loginId;

  @NotBlank(message = "비밀번호는 필수입니다.")
  private String password;

  @NotBlank(message = "역할은 필수입니다.")
  private String role;

}
