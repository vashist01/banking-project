package com.customer.repository;

import com.customer.entity.Customer;
import com.customer.enums.CustomerStatus;
import com.customer.enums.KYCStatus;
import feign.Param;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long> {
    boolean existsByEmail(String email);

    @EntityGraph(attributePaths = {
      "addresss"
    })
    @Transactional(readOnly = true)
    Optional<Customer> findById(Long id);

    List<Customer> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String searchTerm, String searchTerm1);

    @EntityGraph(attributePaths = {
      "addresss"
    })
    @Transactional(readOnly = true)
    Optional<Customer> findByCustomerId(String customerNumber);

     @EntityGraph(attributePaths = {
      "addresss"
    })
    @Transactional(readOnly = true)
    List<Customer> findByStatus(CustomerStatus status);
    
     @EntityGraph(attributePaths = {
      "addresss"
    })
    @Transactional(readOnly = true)

    List<Customer> findByKycStatus(KYCStatus status);

    @Query("SELECT c from Customer c where c.kycStatus=:status and c.active=true")
    List<Customer> findActiveCustomersWithKYCStatus(KYCStatus status);

@Query("SELECT COUNT(c) From Customer c where c.createdAt BETWEEN :startDate and :endDate")
    Long countCustomersCreatedBetween(@Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);
@Query("From Customer c where c.id IN(:ids)")
  List<Customer> findByIds(@Param("ids") List<String> ids);

@Query("SELECT c.customerId from Customer c where email=:email")
  CustomerProjection getCustomerIdByEmail(@Param("email") String email);
}
