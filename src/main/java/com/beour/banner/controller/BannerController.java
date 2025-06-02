package com.beour.banner.controller;

import com.beour.banner.dto.BannerListForUserResponseDto;
import com.beour.banner.dto.BannerListResponseDto;
import com.beour.banner.dto.CreateBannerRequestDto;
import com.beour.banner.dto.CreateBannerResponseDto;
import com.beour.banner.service.BannerService;
import com.beour.global.response.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class BannerController {

    private final BannerService bannerService;

    @PostMapping("/admin/banner/create")
    public ApiResponse<CreateBannerResponseDto> createBanner(@Valid @RequestBody
    CreateBannerRequestDto requestDto) {
        return ApiResponse.ok(bannerService.createBanner(requestDto));
    }

    @GetMapping("/admin/banner/list")
    public ApiResponse<List<BannerListResponseDto>> getBannerList(){
        return ApiResponse.ok(bannerService.getBannerList());
    }

    @GetMapping("/api/banners")
    public ApiResponse<List<BannerListForUserResponseDto>> userGetBannerList(){
        return ApiResponse.ok(bannerService.getBannerListForUser());
    }

}
