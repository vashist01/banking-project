package com.auth.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResponseMessageEnum {
    SUCCESS("Success"),
    ALREADY_EXIST("User Already Register with This Mobile Number Please user other number"),
    FAILURE("Failure");

    private final String message;
}
