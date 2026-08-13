package com.banking.account.publisher;

import com.banking.account.config.SnsPropertyConfig;
import com.banking.account.event.PaymentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class SnsPublisher {

  private final SnsClient snsClient;

  private final SnsPropertyConfig snsProperties;
  private final ObjectMapper objectMapper;
  public boolean publishSNS(PaymentEvent paymentEvent){
    try{
      PublishRequest publishRequest = PublishRequest.builder()
          .message(objectMapper.writeValueAsString(paymentEvent)).topicArn(snsProperties.getTopicArn()).build();
      PublishResponse publishResponse = snsClient.publish(publishRequest);
      return publishResponse.messageId() != null;
    }catch (Exception exception){
      log.error("Failed to send notification : {}",exception.getMessage());
      return false;
    }
  }

}
