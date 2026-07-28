package com.banking.fraud.detection.dto;

public record FraudCheckResult(boolean fraud, String reason) {
}
