package com.audit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "audit")
public class Audit {

  @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "transaction_id")
  private String transactionId;

  @Column(name = "sender_account_number")
  private String senderAccountNumber;

  @Column(name = "sender_customer_id")
  private Long senderCustomerId;

  @Column(name = "receiver_account_number")
  private String receiverAccountNumber;

  @Column(name = "receiver_customer_id")
  private Long receiverCustomerId;

  @Column(name = "amount", precision = 19, scale = 2)
  private BigDecimal amount;

  @Column(name = "currency", length = 10)
  private String currency;

  @Column(name = "remarks", length = 255)
  private String remarks;

  @Column(name = "transaction_time")
  private LocalDateTime transactionTime;
}