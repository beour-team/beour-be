package com.beour.space.guest.controller;

import com.beour.global.response.ApiResponse;
import com.beour.space.domain.entity.Space;
import com.beour.space.guest.dto.FilteringSearchRequestDto;
import com.beour.space.guest.dto.NearbySpaceResponse;
import com.beour.space.guest.dto.SearchSpaceResponseDto;
import com.beour.space.guest.service.GuestSpaceSearchService;
import com.beour.space.guest.service.GuestSpaceService;
import com.beour.space.host.enums.SpaceCategory;
import com.beour.space.host.enums.UseCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/spaces")
public class GuestSpaceController {
    private final GuestSpaceService guestSpaceService;
    private final GuestSpaceSearchService guestSpaceSearchService;

    @GetMapping("/nearby")
    public ResponseEntity<List<NearbySpaceResponse>> getNearbySpaces(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam double radiusKm,
            @RequestParam long userId
    ) {
        List<NearbySpaceResponse> response = guestSpaceService.findNearbySpaces(latitude, longitude, radiusKm, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ApiResponse<List<SearchSpaceResponseDto>> searchSpaces(@RequestParam(value = "request") String request){
        List<Space> spaces = guestSpaceSearchService.search(request);

        List<SearchSpaceResponseDto> response = spaces.stream()
            .map(SearchSpaceResponseDto::of)
            .toList();

        return ApiResponse.ok(response);
    }

    @PostMapping("/search/filter")
    public ApiResponse<List<SearchSpaceResponseDto>> searchSpacesWithFiltering(@RequestBody FilteringSearchRequestDto requestDto){
        return ApiResponse.ok(guestSpaceSearchService.searchWithFiltering(requestDto));
    }

    @GetMapping("/search/spacecategory")
    public ApiResponse<List<SearchSpaceResponseDto>> searchWithSpaceCategory(@RequestParam(value = "spacecategory")
        SpaceCategory request){

        return ApiResponse.ok(guestSpaceSearchService.searchSpaceWithSpaceCategory(request));
    }

    @GetMapping("/search/usecategory")
    public ApiResponse<List<SearchSpaceResponseDto>> searchWithUseCategory(@RequestParam(value = "usecategory")
    UseCategory request){

        return ApiResponse.ok(guestSpaceSearchService.searchSpaceWithUseCategory(request));
    }

}
