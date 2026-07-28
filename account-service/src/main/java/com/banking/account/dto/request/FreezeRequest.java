package com.banking.account.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FreezeRequest {
    @NotBlank(message = "Freeze reason is required")
    private String reason;
    @NotBlank(message = "Account is required")
    private String accountNumber;
    private String notes;
}