package com.beour.space.guest.dto;

import com.beour.space.domain.entity.Space;
import com.beour.space.domain.entity.Tag;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SpaceListSpaceResponseDto {

    private Long spaceId;
    private String spaceName;
    private String region;
    private int maxCapacity;
    private int price;
    private String thumbnailUrl;
    private boolean like;
    private double average;
    private List<String> tags;

    @Builder
    private SpaceListSpaceResponseDto(Long spaceId, String spaceName, String region, int maxCapacity, int price, String thumbnailUrl, boolean like, double average, List<String> tags){
        this.spaceId = spaceId;
        this.spaceName = spaceName;
        this.region = region;
        this.maxCapacity = maxCapacity;
        this.price = price;
        this.thumbnailUrl = thumbnailUrl;
        this.like = like;
        this.average = average;
        this.tags = tags;
    }

    public static SpaceListSpaceResponseDto of(Space space, boolean like){
        List<String> tagList = space.getTags().stream()
            .map(Tag::getContents)
            .collect(Collectors.toList());

        return SpaceListSpaceResponseDto.builder()
            .spaceId(space.getId())
            .spaceName(space.getName())
            .region(space.getAddress())
            .maxCapacity(space.getMaxCapacity())
            .price(space.getPricePerHour())
            .thumbnailUrl(space.getThumbnailUrl())
            .like(like)
            .average(space.getAvgRating())
            .tags(tagList)
            .build();
    }

}
