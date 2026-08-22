package com.banking.account.repository;

import com.banking.account.entity.Account;
import com.banking.account.enums.AccountType;
import com.banking.account.repository.projection.BalanceProjection;
import com.banking.account.repository.projection.DailyTransferLimitProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account,String> {
    Optional<Account> findByAccountNumber(String accountNumber);

    @Query("SELECT ac.balance as balance FROM Account ac WHERE ac.accountNumber = :accountNumber")
    BalanceProjection findBalanceProjectionByAccountNumber(@Param("accountNumber") String accountNumber);

    Optional<Account> findByCustomerIdAndAccountType(String customerId, AccountType saving);
    @Query("SELECT ac.dailyTransferLimit from Account ac WHERE ac.accountNumber=:accountNumber")
    DailyTransferLimitProjection findDailyTransferLimitByAccountNumber(String senderAccountNumber);


}
