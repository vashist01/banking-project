package com.banking.account.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkTransferRequest {
    
    @NotBlank(message = "Source account ID is required")
    private String sourceId;
    
    @NotEmpty(message = "At least one transfer is required")
    @Valid
    private List<TransferItem> transfers;
}