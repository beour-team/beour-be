package com.beour.global.exception.error.errorcode;

import com.beour.global.exception.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SpaceErrorCode implements ErrorCode {

    SPACE_NOT_FOUND(404, "존재하지 않는 공간입니다."),
    NO_MATCHING_SPACE(404, "해당 조건에 일치하는 공간이 없습니다."),
    NO_RECENT_SPACE(404, "최근 등록된 공간이 없습니다."),
    NO_HOST_SPACE(404, "해당 호스트가 등록한 공간이 없습니다."),
    KEYWORD_REQUIRED(400, "키워드를 입력해주세요"),
    NO_PERMISSION(401, "해당 공간에 대한 권한이 없습니다."),
    INVALID_SPACE_CATEGORY(400, "유효하지 않은 공간 유형입니다."),
    INVALID_USE_CATEGORY(400, "유효하지 않은 사용 용도입니다.");


    private final Integer code;
    private final String message;
}
