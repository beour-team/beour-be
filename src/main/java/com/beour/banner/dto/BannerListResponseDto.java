package com.beour.banner.dto;

import com.beour.banner.entity.Banner;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BannerListResponseDto {

    private String imgUrl;
    private String linkUrl;
    private String title;
    private Boolean isActive;
    private int displayOrder;
    private LocalDate startDate;
    private LocalDate endDate;

    public static BannerListResponseDto dtoFrom(Banner banner){
        return BannerListResponseDto.builder()
            .imgUrl(banner.getImgUrl())
            .linkUrl(banner.getLinkUrl())
            .title(banner.getTitle())
            .isActive(banner.isActive())
            .displayOrder(banner.getDisplayOrder())
            .startDate(banner.getStartDate())
            .endDate(banner.getEndDate())
            .build();
    }

}
