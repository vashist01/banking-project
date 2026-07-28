package com.banking.transaction.entity;

import com.banking.transaction.enums.TransactionStatusEnum;
import com.banking.transaction.enums.TransactionTypeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "transaction",indexes = {
        @Index(name = "idx_sender_account_number",columnList="sender_account_number"),
        @Index(name = "idx_receiver_account_number",columnList = "receiver_account_number")
})
public class Transaction{

    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "sender_account_number",length = 50)
    private String senderAccountNumber;
    @Column(name = "receiver_account_number",length = 15)
    private String receiverAccountNumber;
    private BigDecimal amount;
    @Column(name = "transaction_type",length = 20)
    @Enumerated(EnumType.STRING)
    private TransactionTypeEnum transactionType;
    @Column(name = "transaction_status",length = 20)
    @Enumerated(EnumType.STRING)
    private TransactionStatusEnum transactionStatus;
    private String description;
    @Column(name = "failure_reason")
    private String failureReason;
    @Column(name = "reference_number")
    private String referenceNumber;
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Version
    private long version;
}
