package com.customer.repository;

import com.customer.entity.Customer;
import com.customer.enums.CustomerStatus;
import com.customer.enums.KYCStatus;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long> {
    boolean existsByEmail(String email);

    List<Customer> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String searchTerm, String searchTerm1);

    Optional<Customer> findByCustomerNumber(String customerNumber);

    List<Customer> findByStatus(CustomerStatus status);

    List<Customer> findByKycStatus(KYCStatus status);

    @Query("SELECT c from Customer c where c.kycStatus=:status and c.active=true")
    List<Customer> findActiveCustomersWithKYCStatus(KYCStatus status);

@Query("SELECT COUNT(c) From Customer c where c.createdAt BETWEEN :startDate and :endDate")
    Long countCustomersCreatedBetween(@Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);
}
