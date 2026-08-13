package com.banking.account.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("aws.sns")
@Data
public class SnsPropertyConfig {
  private String topicArn;
}
