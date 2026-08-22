package com.banking.account.entity;

import com.banking.account.enums.AccountStatus;
import com.banking.account.enums.AccountType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor; 

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "account",indexes = {
        @Index(name = "idx_account_account_number", columnList = "account_number")
})
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Account extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private String customerId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Column(nullable = false)
    private String currency;

    private String accountName;
    private String description;

    @Column(nullable = false)
    private BigDecimal availableBalance;

    @Column(nullable = false)
    private BigDecimal holdBalance;

    @Column(nullable = false)
    private BigDecimal overdraftLimit;

    private LocalDateTime openedDate;
    private LocalDateTime closedDate;

    @ElementCollection
    @CollectionTable(name = "account_holders",
            joinColumns = @JoinColumn(name = "account_id"))
    @Column(name = "holder_id")
    private List<String> jointHolders = new ArrayList<>();

    @Embedded // @Embeddable का उपयोग JPA/Hibernate में ऐसी class बनाने के लिए किया जाता है जो अपनी अलग table
    // नहीं बनाती, बल्कि उसके fields किसी दूसरी Entity की table में embed हो जाते हैं।
    private AccountLimits limits;

    @Column(nullable = false)
    private BigDecimal dailyWithdrawalLimit;

    private BigDecimal dailyTransferLimit;

    @Column(nullable = false)
    private boolean frozen;

    private String freezeReason;
    private LocalDateTime frozenAt;
    private String frozenBy;

    @Column(nullable = false)
    private boolean active;

    private LocalDateTime lastTransactionAt;
    private LocalDateTime lastInterestCalculatedAt;



    @Version
    private Long version;

    public void deposit(BigDecimal initialBalance) {
        if (initialBalance.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        if (!active || status != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Account is not active");
        }
        if (frozen) {
            throw new IllegalStateException("Account is frozen");
        }

        this.balance = this.balance.add(initialBalance);
        this.availableBalance = this.availableBalance.add(initialBalance);
        this.lastTransactionAt = LocalDateTime.now();

    }
}
