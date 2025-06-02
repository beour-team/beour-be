package com.beour.banner.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateBannerRequestDto {

    @NotBlank(message = "이미지를 등록해주세요.")
    private String imgUrl;

    @NotBlank(message = "이동링크를 등록해주세요.")
    private String linkUrl;

    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    @NotNull(message = "활성화를 선택해주세요.")
    private Boolean isActive;

    @NotNull(message = "순서를 입력해주세요.")
    private int displayOrder;

    @NotNull(message = "배너 시작 날짜를 입력해주세요.")
    private LocalDate startDate;

    @NotNull(message = "배너 마지막 날짜를 입력해주세요.")
    private LocalDate endDate;

}
