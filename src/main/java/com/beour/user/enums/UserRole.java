package com.beour.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {

    ADMIN("관리자"),
    GUEST("공간 대여자"),
    HOST("공간 제공자");

    private final String test;
}
