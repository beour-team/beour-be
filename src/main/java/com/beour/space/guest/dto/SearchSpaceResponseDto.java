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
public class SearchSpaceResponseDto {

    private Long spaceId;
    private String spaceName;
    private String thumbnailUrl;
    private int price;
    private String description;
    private List<String> tags;

    @Builder
    private SearchSpaceResponseDto(Long spaceId, String spaceName, String thumbnailUrl, int price, String description, List<String> tags){
        this.spaceId = spaceId;
        this.spaceName = spaceName;
        this.thumbnailUrl = thumbnailUrl;
        this.price = price;
        this.description = description;
        this.tags = tags;
    }

    public static SearchSpaceResponseDto of(Space space){
        List<String> tagList = space.getTags().stream()
            .map(Tag::getContents)
            .collect(Collectors.toList());

        return SearchSpaceResponseDto.builder()
            .spaceId(space.getId())
            .spaceName(space.getName())
            .thumbnailUrl(space.getThumbnailUrl())
            .price(space.getPricePerHour())
            .description(space.getDescription().getDescription())
            .tags(tagList)
            .build();
    }
}
