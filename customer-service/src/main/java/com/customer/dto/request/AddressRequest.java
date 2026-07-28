package com.customer.dto.request;

import com.customer.enums.AddressType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public  class AddressRequest {
        @NotBlank(message = "Address line 1 is required")
        private String addressLine1;

        private String addressLine2;

        @NotBlank(message = "City is required")
        private String city;

        @NotBlank(message = "State is required")
        private String state;

        @NotBlank(message = "Postal code is required")
        @Pattern(regexp = "^[0-9]{5}(-[0-9]{4})?$", message = "Invalid postal code format")
        private String postalCode;

        @NotBlank(message = "Country is required")
        private String country;

        private boolean primaryAddress;

        @NotNull(message = "Address type is required")
        private AddressType type;
    }