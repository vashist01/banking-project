package com.banking.account.repository;

import com.banking.account.entity.OutBoxPattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OutBoxPatternRepository extends JpaRepository<OutBoxPattern,Long> {

}
