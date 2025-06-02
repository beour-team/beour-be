package com.beour.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class UserInformationDetailResponseDto {

    private String name;
    private String email;
    private String nickName;
    private String phoneNum;

}
