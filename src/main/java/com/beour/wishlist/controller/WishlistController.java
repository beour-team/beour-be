package com.beour.wishlist.controller;

import com.beour.global.response.ApiResponse;
import com.beour.space.guest.dto.SpaceListSpaceResponseDto;
import com.beour.wishlist.dto.MakeWishlistResponseDto;
import com.beour.wishlist.entity.Like;
import com.beour.wishlist.service.WishlistService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/wishlist")
@RestController
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    public ApiResponse<MakeWishlistResponseDto> addSpaceToWishlist(@RequestParam(value = "spaceId")Long spaceId){
        Like like = wishlistService.addSpaceToWishList(spaceId);
        MakeWishlistResponseDto dto = new MakeWishlistResponseDto("찜 등록이 완료되었습니다.", like.getId());
        return ApiResponse.ok(dto);
    }

    @DeleteMapping
    public ApiResponse<String> deleteSpaceFromWishlist(@RequestParam(value = "spaceId")Long spaceId){
        wishlistService.deleteSpaceFromWishList(spaceId);

        return ApiResponse.ok("찜 삭제가 되었습니다.");
    }

    @GetMapping("/all")
    public ApiResponse<List<SpaceListSpaceResponseDto>> getWishlist(){
        return ApiResponse.ok(wishlistService.getWishlist());
    }

}
