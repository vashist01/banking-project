package com.banking.account.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class SnsConfig {

  @Bean
  public SnsClient snsClient(){
    return SnsClient.builder().region(Region.AP_SOUTH_1).build();
  }

  @Bean
  @ConditionalOnMissingBean //Create this bean only if another bean of the same type doesn't already exist."
  public ObjectMapper objectMapper(){
    return new ObjectMapper();
  }
}
