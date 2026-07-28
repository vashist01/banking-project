package com.banking.transaction.repository;

import com.banking.transaction.entity.TransactionOutBox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionOutBoxRepository extends JpaRepository<TransactionOutBox,Long> {

    List<TransactionOutBox> findAllByTransactionStatus(String name);
}
