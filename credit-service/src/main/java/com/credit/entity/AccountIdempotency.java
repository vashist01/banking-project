package com.credit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "idempotent_validation",indexes = {
    @Index(name = "idx_trx_transactionId", columnList = "transaction_id")
})
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountIdempotency {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false)
  private Long id;

  private String transactionId;

  private String senderAccountNumber;

  private Long senderCustomerId;

  private String receiverAccountNumber;

  private Long receiverCustomerId;

  private BigDecimal amount;

  private String currency;

  private String remarks;

  private LocalDateTime transactionTime;
}
