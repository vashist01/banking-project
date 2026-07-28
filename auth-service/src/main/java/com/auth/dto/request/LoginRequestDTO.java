package com.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record LoginRequestDTO(
        @JsonProperty("mobile_number")
        @NotBlank(message = "mobile_number is required")
        String mobileNumber) {
}
