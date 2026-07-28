package com.banking.payment.repository;

import com.banking.payment.entity.OutBoxPattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutBoxRepository extends JpaRepository<OutBoxPattern,Long> {
    List<OutBoxPattern> findByStatus(String processing);
}
