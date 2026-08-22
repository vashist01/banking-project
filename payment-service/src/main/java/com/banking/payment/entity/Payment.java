package com.banking.payment.entity;

import com.banking.payment.enums.PaymentStatus;
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
@Table
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Payment {
    @Id@GeneratedValue(strategy = GenerationType.IDENTITY )
    private long id;
    @Column(name = "rozar_pay_order_id")
    private String razorpayOrderId;
    @Column(name = "rozar_pay_payment_id")
    private String razorpayPaymentId;
    @Column(name = "account_number")
    private String accountNumber;
    private BigDecimal amount;
    @Column(name = "payment_status")
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    private String currency;
    private String description;
    @Column(name = "failed_reason")
    private String failedReason;
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "payment_type")
    private String paymentType;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
