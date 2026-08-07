package com.audit.listener;

import com.audit.service.AuditEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
@Component
@RequiredArgsConstructor
@Slf4j
public class AccountEventConsumer {
    private final AuditEventService auditEventService;
    @KafkaListener(topics = "audit-event",groupId = "audit-service-group")
    public void  accountEvent(@Payload AuditEvent auditEvent){
      auditEventService.saveEvent(auditEvent);
    }

  @KafkaListener(topics = "audit-event.DLT",groupId = "audit-service-group")
  public void  dlqEvent(@Payload AuditEvent auditEvent){
    auditEventService.saveEvent(auditEvent);
  }

}
