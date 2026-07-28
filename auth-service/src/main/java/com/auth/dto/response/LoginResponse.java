package com.auth.dto.response;

import lombok.Builder;

@Builder
public record LoginResponse(String token) {
}
