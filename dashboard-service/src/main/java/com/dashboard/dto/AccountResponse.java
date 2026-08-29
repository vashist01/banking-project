package com.dashboard.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

/**
 * AccountResponse
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {
  private String id;
  private String accountNumber;
  private String customerId;
  private String accountType;
  private String status;
  private BigDecimal balance;
  private BigDecimal availableBalance;
  private BigDecimal holdBalance;
  private BigDecimal overdraftLimit;
  private String currency;
  private String accountName;
  private boolean frozen;
  private LocalDateTime openedDate;
  private List<String> jointHolders;
  private String limits;
  private LocalDateTime createdAt;
}