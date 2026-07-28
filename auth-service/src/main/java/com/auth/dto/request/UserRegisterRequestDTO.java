package com.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record UserRegisterRequestDTO(
        String firstName,
     String email,
    @JsonProperty("phone_number") String phoneNumber) {
}
