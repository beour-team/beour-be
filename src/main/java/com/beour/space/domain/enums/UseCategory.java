package com.beour.space.domain.enums;

import com.beour.global.exception.error.errorcode.SpaceErrorCode;
import com.beour.global.exception.exceptionType.InvalidCategoryException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum UseCategory {
    MEETING("단체 모임"),
    COOKING("요리 연습"),
    BARISTA("바리스타 실습"),
    FLEA_MARKET("플리마켓"),
    FILMING("촬영"),
    ETC("기타");

    private final String displayName;

    public static UseCategory fromDisplayName(String displayName) {
        return Arrays.stream(UseCategory.values())
                .filter(c -> c.getDisplayName().equals(displayName))
                .findFirst()
                .orElseThrow(() -> new InvalidCategoryException(
                        SpaceErrorCode.INVALID_USE_CATEGORY,
                        "INVALID_USE_CATEGORY"
                ));
    }
}
