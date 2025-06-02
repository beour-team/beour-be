package com.beour.user.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UpdateUserInfoRequestDto {

    private String newNickname;

    @Pattern(regexp = "^\\d{10,11}$", message = "전화번호는 숫자만 10~11자리로 입력하세요.")
    private String newPhone;

}
