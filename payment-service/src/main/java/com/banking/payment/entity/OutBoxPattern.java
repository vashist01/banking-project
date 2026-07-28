package com.banking.payment.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "out_box_pattern")
@Data
public class OutBoxPattern {
    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "payment_id")
    private String paymentId;
    @Column(name = "account_number")
    private String accountNumber;
    private BigDecimal amount;
    @Column(name = "razor_payment_id")
    private String razorPaymentId;
    private String status;
}
