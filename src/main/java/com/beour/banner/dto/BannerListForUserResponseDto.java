package com.beour.banner.dto;

import com.beour.banner.entity.Banner;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BannerListForUserResponseDto {

    private Long bannerId;
    private String imageUrl;
    private String linkUrl;

    public static BannerListForUserResponseDto dtoFrom(Banner banner){
        return BannerListForUserResponseDto.builder()
            .bannerId(banner.getId())
            .imageUrl(banner.getImgUrl())
            .linkUrl(banner.getLinkUrl())
            .build();
    }
}
