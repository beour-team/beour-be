package com.beour.space.domain.enums;

import com.beour.global.exception.error.errorcode.SpaceErrorCode;
import com.beour.global.exception.exceptionType.InvalidCategoryException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum SpaceCategory {
    CAFE("카페"),
    RESTAURANT("식당"),
    COOKING("쿠킹 공방"),
    LEATHER("가죽 공방"),
    COSTUME("의상 공방"),
    ART("아트 공방"),
    ETC("기타");

    private final String displayName;

    public static SpaceCategory fromDisplayName(String displayName) {
        return Arrays.stream(SpaceCategory.values())
                .filter(c -> c.getDisplayName().equals(displayName))
                .findFirst()
                .orElseThrow(() -> new InvalidCategoryException(
                        SpaceErrorCode.INVALID_SPACE_CATEGORY,
                        "INVALID_SPACE_CATEGORY"
                ));
    }
}
