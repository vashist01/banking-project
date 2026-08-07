package com.banking.transaction.repository;

import com.banking.transaction.entity.Transaction;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,String> {
    List<Transaction> findBySenderAccountNumberOrderByCreatedAtDesc(String senderAccountNumber);

//    List<Transaction> findAllByTransactionStatus(String name);
//
//    List<Transaction> findByAccountNumber(String accountNumber, LocalDateTime startDate, LocalDateTime endDate);

  Optional<Transaction> findByTransactionId(String transactionId);
}
