package com.banking.account.repository;

import com.banking.account.entity.OutBoxPattern;
import feign.Param;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OutBoxPatternRepository extends JpaRepository<OutBoxPattern,Long> {
  @Query("From OutBoxPattern obp where obp.eventStatus=:status")
  List<OutBoxPattern> findByStatus(@Param("status") String status);
}
