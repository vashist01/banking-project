package com.banking.transaction.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionOutBox {
    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "transaction_id")
    private String transactionId;
    @Column(name="sender_account_number",length = 15)
    private String senderAccountNumber;
    @Column(name="receiver_account_number",length = 15)
    private String receiverAccountNumber;
    private BigDecimal amount;
    private String description;
    @Column(name = "transaction_status")
    private String transactionStatus;
}
