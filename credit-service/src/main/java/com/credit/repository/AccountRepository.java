package com.credit.repository;


import com.credit.entity.Account;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account,String> {
    Optional<Account> findByAccountNumber(String accountNumber);
    @Query("From Account ac where ac.accountNumber=:accountNumber AND ac.customerId=:customerId")
    Optional<Account> findByAccountNumberAndCustomerId(@Param("accountNumber") String receiverAccountNumber,
        @Param("customerId") String customerId);
}
