package com.audit.service;

import com.audit.entity.Audit;
import com.audit.listener.AuditEvent;
import com.audit.repository.AuditRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditEventService {
private final AuditRepository auditRepository;
  @Transactional
  public void saveEvent(AuditEvent auditEvent) {
    Audit audit = new Audit();
    audit.setAmount(auditEvent.getAmount());
    audit.setCurrency(Optional.ofNullable(auditEvent.getCurrency()).orElse("INR"));
    audit.setRemarks(Optional.ofNullable(auditEvent.getRemarks()).orElse(null));
    audit.setSenderAccountNumber(auditEvent.getSenderAccountNumber());
    audit.setReceiverAccountNumber(auditEvent.getReceiverAccountNumber());
    audit.setSenderCustomerId(auditEvent.getSenderCustomerId());
    audit.setTransactionId(auditEvent.getTransactionId());
    audit.setReceiverCustomerId(auditEvent.getReceiverCustomerId());
    audit.setTransactionTime(LocalDateTime.now());
    auditRepository.save(audit);
  }
}
