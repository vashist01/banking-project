package com.banking.fraud.detection.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class PropertyConfig {

    private final  int maxCountPerMinute;
    private final long suspiciousnessMultiplier;
    private final String maxBalancePercentage;

    public PropertyConfig( @Value("${fraud.max-transaction-per-minute}") int maxCountPerMinute,
                           @Value("${fraud.suspicious-amount-multiplier}") long suspiciousnessMultiplier,
                           @Value("${fraud.max-balance-percentage}") String maxBalancePercentage) {
        this.maxCountPerMinute = maxCountPerMinute;
        this.suspiciousnessMultiplier = suspiciousnessMultiplier;
        this.maxBalancePercentage = maxBalancePercentage;
    }
}
