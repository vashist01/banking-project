package com.auth.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ServiceNameEnum {

    USER_SERVICE("user-service"), PAYMENT_SERVICE("payment-service");

    private final String serviceName;
}
