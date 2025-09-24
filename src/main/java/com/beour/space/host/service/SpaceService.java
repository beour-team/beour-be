package com.beour.space.host.service;

import com.beour.global.exception.error.errorcode.SpaceErrorCode;
import com.beour.global.exception.error.errorcode.UserErrorCode;
import com.beour.global.exception.exceptionType.SpaceNotFoundException;
import com.beour.global.exception.exceptionType.UnauthorityException;
import com.beour.global.exception.exceptionType.UserNotFoundException;
import com.beour.global.file.ImageUploader;
import com.beour.review.domain.repository.ReviewRepository;
import com.beour.space.domain.entity.*;
import com.beour.space.domain.enums.SpaceCategory;
import com.beour.space.domain.enums.UseCategory;
import com.beour.space.domain.repository.*;
import com.beour.space.host.dto.*;
import com.beour.user.entity.User;
import com.beour.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpaceService {

    private final SpaceRepository spaceRepository;
    private final DescriptionRepository descriptionRepository;
    private final TagRepository tagRepository;
    private final SpaceImageRepository spaceImageRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final KakaoMapService kakaoMapService;
    private final ImageUploader imageUploader;

    @Transactional
    public Long registerSpace(SpaceRegisterRequestDto dto, MultipartFile thumbnailFile, List<MultipartFile> imageFiles) throws IOException {
        User host = findUserFromToken();
        double[] latitudeAndLongitude = kakaoMapService.getLatitudeAndLongitude(dto.getAddress());

        // 썸네일 이미지 업로드
        String thumbnailUrl = null;
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            thumbnailUrl = imageUploader.upload(thumbnailFile);
        }

        // 1. Space
        Space space = Space.builder()
                .host(host)
                .name(dto.getName())
                .spaceCategory(SpaceCategory.fromDisplayName(dto.getSpaceCategory()))
                .useCategory(UseCategory.fromDisplayName(dto.getUseCategory()))
                .maxCapacity(dto.getMaxCapacity())
                .address(dto.getAddress())
                .detailAddress(dto.getDetailAddress())
                .pricePerHour(dto.getPricePerHour())
                .thumbnailUrl(thumbnailUrl)
                .latitude(latitudeAndLongitude[0])
                .longitude(latitudeAndLongitude[1])
                .avgRating(0.0)
                .build();
        spaceRepository.save(space);

        // 2. Description
        Description description = Description.builder()
                .space(space)
                .description(dto.getDescription())
                .priceGuide(dto.getPriceGuide())
                .facilityNotice(dto.getFacilityNotice())
                .notice(dto.getNotice())
                .locationDescription(dto.getLocationDescription())
                .refundPolicy(dto.getRefundPolicy())
                .build();
        descriptionRepository.save(description);

        // 3. Tags
        if (dto.getTags() != null && !dto.getTags().isEmpty()) {
            List<Tag> tags = dto.getTags().stream()
                    .map(content -> Tag.builder().space(space).contents(content).build())
                    .toList();
            tagRepository.saveAll(tags);
        }

        // 4. SpaceImages - 다중 파일 업로드
        if (imageFiles != null && !imageFiles.isEmpty()) {
            List<String> imageUrls = new ArrayList<>();
            for (MultipartFile file : imageFiles) {
                if (!file.isEmpty()) {
                    String imageUrl = imageUploader.upload(file);
                    imageUrls.add(imageUrl);
                }
            }

            if (!imageUrls.isEmpty()) {
                List<SpaceImage> images = imageUrls.stream()
                        .map(url -> SpaceImage.builder().space(space).imageUrl(url).build())
                        .toList();
                spaceImageRepository.saveAll(images);
            }
        }

        return space.getId();
    }

    @Transactional(readOnly = true)
    public SpaceSimpleResponseDto getSimpleSpaceInfo(Long spaceId) {
        Space space = spaceRepository.findByIdAndDeletedAtIsNull(spaceId)
                .orElseThrow(() -> new SpaceNotFoundException(SpaceErrorCode.SPACE_NOT_FOUND));

        List<String> tagContents = space.getTags().stream()
                .map(Tag::getContents)
                .collect(Collectors.toList());

        return new SpaceSimpleResponseDto(
                space.getName(),
                extractDongFromAddress(space.getAddress()),
                space.getPricePerHour(),
                tagContents,
                space.getThumbnailUrl()
        );
    }

    @Transactional(readOnly = true)
    public SpaceDetailResponseDto getDetailedSpaceInfo(Long spaceId) {
        Space space = spaceRepository.findByIdAndDeletedAtIsNull(spaceId)
                .orElseThrow(() -> new SpaceNotFoundException(SpaceErrorCode.SPACE_NOT_FOUND));

        Description desc = space.getDescription();

        return SpaceDetailResponseDto.builder()
                .id(space.getId())
                .name(space.getName())
                .address(space.getAddress())
                .detailAddress(space.getDetailAddress())
                .pricePerHour(space.getPricePerHour())
                .maxCapacity(space.getMaxCapacity())
                .spaceCategory(space.getSpaceCategory().getDisplayName())
                .useCategory(space.getUseCategory().getDisplayName())
                .avgRating(space.getAvgRating())
                .thumbnailUrl(space.getThumbnailUrl())
                .description(desc.getDescription())
                .priceGuide(desc.getPriceGuide())
                .facilityNotice(desc.getFacilityNotice())
                .notice(desc.getNotice())
                .locationDescription(desc.getLocationDescription())
                .refundPolicy(desc.getRefundPolicy())
                .tags(space.getTags().stream().map(Tag::getContents).toList())
                .imageUrls(space.getSpaceImages().stream().map(SpaceImage::getImageUrl).toList())
                .build();
    }

    @Transactional(readOnly = true)
    public HostMySpaceListPageResponseDto getMySpaces(Pageable pageable) {
        User host = findUserFromToken();

        Page<Space> spacePage = spaceRepository.findByHostAndDeletedAtIsNull(host, pageable);

        if (spacePage.isEmpty()) {
            throw new IllegalStateException("조회된 공간이 없습니다.");
        }

        List<HostMySpaceListResponseDto> spaces = spacePage.getContent().stream()
                .map(space -> {
                    long reviewCount = getReviewCountBySpaceId(space.getId());
                    return HostMySpaceListResponseDto.of(
                            space.getId(),
                            space.getName(),
                            extractDongFromAddress(space.getAddress()),
                            space.getMaxCapacity(),
                            space.getAvgRating(),
                            reviewCount,
                            space.getThumbnailUrl()
                    );
                })
                .collect(Collectors.toList());

        return new HostMySpaceListPageResponseDto(
                spaces,
                spacePage.isLast(),
                spacePage.getTotalPages()
        );
    }

    @Transactional
    public void updateSpace(Long spaceId, SpaceUpdateRequestDto dto, MultipartFile thumbnailFile, List<MultipartFile> imageFiles) throws IOException {
        Space space = findSpaceByIdAndCheckOwnership(spaceId);
        double[] latitudeAndLongitude = kakaoMapService.getLatitudeAndLongitude(dto.getAddress());

        // 썸네일 이미지 업로드 (새 파일이 있는 경우에만)
        String thumbnailUrl = space.getThumbnailUrl(); // 기존 URL 유지
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            thumbnailUrl = imageUploader.upload(thumbnailFile);
        }

        // 1. Space 수정
        space.update(
                dto.getName(), dto.getAddress(), dto.getDetailAddress(), dto.getPricePerHour(),
                dto.getMaxCapacity(), SpaceCategory.fromDisplayName(dto.getSpaceCategory()), UseCategory.fromDisplayName(dto.getUseCategory()),
                thumbnailUrl, latitudeAndLongitude[0], latitudeAndLongitude[1]
        );

        // 2. Description 수정
        Description desc = space.getDescription();
        desc.update(
                dto.getDescription(), dto.getPriceGuide(), dto.getFacilityNotice(), dto.getNotice(),
                dto.getLocationDescription(), dto.getRefundPolicy()
        );

        // 3. Tags 재저장
        if (dto.getTags() != null) {
            tagRepository.deleteBySpace(space);
            if (!dto.getTags().isEmpty()) {
                List<Tag> tags = dto.getTags().stream()
                        .map(content -> Tag.builder().space(space).contents(content).build())
                        .toList();
                tagRepository.saveAll(tags);
            }
        }

        // 4. Images 재저장 (새 파일들이 있는 경우에만)
        if (imageFiles != null && !imageFiles.isEmpty()) {
            // 기존 이미지들 삭제
            spaceImageRepository.deleteBySpace(space);

            // 새 이미지들 업로드 및 저장
            List<String> imageUrls = new ArrayList<>();
            for (MultipartFile file : imageFiles) {
                if (!file.isEmpty()) {
                    String imageUrl = imageUploader.upload(file);
                    imageUrls.add(imageUrl);
                }
            }

            if (!imageUrls.isEmpty()) {
                List<SpaceImage> images = imageUrls.stream()
                        .map(url -> SpaceImage.builder().space(space).imageUrl(url).build())
                        .toList();
                spaceImageRepository.saveAll(images);
            }
        }
    }

/*    @Transactional
    public void updateSpaceBasic(Long id, SpaceUpdateRequestDto dto) {
        Space space = findSpaceByIdAndCheckOwnership(id);

        if (dto.getName() != null) space.updateName(dto.getName());
        if (dto.getAddress() != null) {
            double[] latLng = kakaoMapService.getLatitudeAndLongitude(dto.getAddress());
            space.updateAddress(dto.getAddress(), latLng[0], latLng[1]);
        }
        if (dto.getDetailAddress() != null) space.updateDetailAddress(dto.getDetailAddress());
        if (dto.getPricePerHour() != 0) space.updatePricePerHour(dto.getPricePerHour());
        if (dto.getMaxCapacity() != 0) space.updateMaxCapacity(dto.getMaxCapacity());
        if (dto.getSpaceCategory() != null) space.updateSpaceCategory(dto.getSpaceCategory());
        if (dto.getUseCategory() != null) space.updateUseCategory(dto.getUseCategory());
    }

    @Transactional
    public void updateSpaceDescription(Long id, SpaceUpdateRequestDto dto) {
        Space space = findSpaceByIdAndCheckOwnership(id);
        Description desc = space.getDescription();

        if (desc != null) {
            if (dto.getDescription() != null) desc.updateDescription(dto.getDescription());
            if (dto.getPriceGuide() != null) desc.updatePriceGuide(dto.getPriceGuide());
            if (dto.getFacilityNotice() != null) desc.updateFacilityNotice(dto.getFacilityNotice());
            if (dto.getNotice() != null) desc.updateNotice(dto.getNotice());
            if (dto.getLocationDescription() != null) desc.updateLocationDescription(dto.getLocationDescription());
            if (dto.getRefundPolicy() != null) desc.updateRefundPolicy(dto.getRefundPolicy());
        }
    }

    @Transactional
    public void updateTags(Long id, SpaceUpdateRequestDto dto) {
        Space space = findSpaceByIdAndCheckOwnership(id);

        if (dto.getTags() != null) {
            tagRepository.deleteBySpace(space);
            if (!dto.getTags().isEmpty()) {
                List<Tag> newTags = dto.getTags().stream()
                        .map(content -> new Tag(space, content))
                        .toList();
                tagRepository.saveAll(newTags);
            }
        }
    }

    @Transactional
    public void updateSpaceImages(Long id, SpaceUpdateRequestDto dto) {
        Space space = findSpaceByIdAndCheckOwnership(id);
        // 이 메서드는 이제 multipart file 업로드 방식으로 대체되었으므로
        // 필요에 따라 제거하거나 다른 용도로 사용할 수 있습니다.
    }*/

    @Transactional
    public void deleteSpace(Long spaceId) {
        Space space = findSpaceByIdAndCheckOwnership(spaceId);
        space.delete();
    }

    // 예: 서울시 강남구 역삼동 어딘가 123 -> 역삼동
    private String extractDongFromAddress(String address) {
        String[] parts = address.split(" ");
        if (parts.length >= 3) {
            String dong = parts[2];
            // '동'이 포함되어 있으면 그대로 반환, 없으면 구까지만 반환
            return dong.contains("동") ? dong : parts[1];
        }
        return address;
    }

    private long getReviewCountBySpaceId(Long spaceId) {
        return reviewRepository.countBySpaceIdAndDeletedAtIsNull(spaceId);
    }

    private Space findSpaceByIdAndCheckOwnership(Long spaceId) {
        User currentUser = findUserFromToken();
        Space space = spaceRepository.findByIdAndDeletedAtIsNull(spaceId)
                .orElseThrow(() -> new SpaceNotFoundException(SpaceErrorCode.SPACE_NOT_FOUND));

        if (!space.getHost().getId().equals(currentUser.getId())) {
            throw new UnauthorityException(SpaceErrorCode.NO_PERMISSION);
        }

        return space;
    }

    private User findUserFromToken() {
        String loginId = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByLoginIdAndDeletedAtIsNull(loginId)
                .orElseThrow(() -> new UserNotFoundException(UserErrorCode.USER_NOT_FOUND));
    }
}
