package com.beour.space.host.controller;

import com.beour.global.response.ApiResponse;
import com.beour.space.host.dto.*;
import com.beour.space.host.service.SpaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class SpaceController {

    private final SpaceService spaceService;

    @PostMapping(value = "/api/spaces", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> registerSpace(
            @Valid @RequestPart("space") SpaceRegisterRequestDto dto,
            @RequestPart(value = "thumbnailFile", required = false) MultipartFile thumbnailFile,
            @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles) throws IOException {

        Long id = spaceService.registerSpace(dto, thumbnailFile, imageFiles);
        return ApiResponse.ok("공간이 등록되었습니다. ID: " + id);
    }

    @GetMapping("/api/spaces/{id}/simple")
    public ApiResponse<SpaceSimpleResponseDto> getSimpleInfo(@PathVariable(value = "id") Long id) {
        return ApiResponse.ok(spaceService.getSimpleSpaceInfo(id));
    }

    @GetMapping("/api/spaces/{id}")
    public ApiResponse<SpaceDetailResponseDto> getDetailInfo(@PathVariable(value = "id") Long id) {
        return ApiResponse.ok(spaceService.getDetailedSpaceInfo(id));
    }

    @GetMapping("/api/users/me/spaces")
    public ApiResponse<HostMySpaceListPageResponseDto> getMySpaces(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ApiResponse.ok(spaceService.getMySpaces(pageable));
    }

    @PutMapping(value = "/api/spaces/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> updateSpace(
            @PathVariable(value = "id") Long id,
            @Valid @RequestPart("space") SpaceUpdateRequestDto dto,
            @RequestPart(value = "thumbnailFile", required = false) MultipartFile thumbnailFile,
            @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles) throws IOException {

        spaceService.updateSpace(id, dto, thumbnailFile, imageFiles);
        return ApiResponse.ok("공간이 성공적으로 수정되었습니다.");
    }

/*    @PatchMapping("/api/spaces/{id}/basic")
    public ApiResponse<String> updateSpaceBasic(@PathVariable(value = "id") Long id,
                                                @Valid @RequestBody SpaceUpdateRequestDto dto) {
        spaceService.updateSpaceBasic(id, dto);
        return ApiResponse.ok("공간 기본 정보가 성공적으로 수정되었습니다.");
    }

    @PatchMapping("/api/spaces/{id}/description")
    public ApiResponse<String> updateSpaceDescription(@PathVariable(value = "id") Long id,
                                                      @Valid @RequestBody SpaceUpdateRequestDto dto) {
        spaceService.updateSpaceDescription(id, dto);
        return ApiResponse.ok("공간 설명이 성공적으로 수정되었습니다.");
    }

    @PatchMapping("/api/spaces/{id}/tags")
    public ApiResponse<String> updateTags(@PathVariable(value = "id") Long id,
                                          @Valid @RequestBody SpaceUpdateRequestDto dto) {
        spaceService.updateTags(id, dto);
        return ApiResponse.ok("태그가 성공적으로 수정되었습니다.");
    }

    @PatchMapping("/api/spaces/{id}/images")
    public ApiResponse<String> updateSpaceImages(@PathVariable(value = "id") Long id,
                                                 @Valid @RequestBody SpaceUpdateRequestDto dto) {
        spaceService.updateSpaceImages(id, dto);
        return ApiResponse.ok("공간 이미지가 성공적으로 수정되었습니다.");
    }*/

    @DeleteMapping("/api/spaces/{id}")
    public ApiResponse<String> deleteSpace(@PathVariable(value = "id") Long id) {
        spaceService.deleteSpace(id);
        return ApiResponse.ok("공간이 성공적으로 삭제되었습니다.");
    }
}
