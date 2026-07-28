package com.banking.payment.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class PropertyConfiguration {

    private final String razorPayKey;
    private final String razorPaySecret;

    public PropertyConfiguration(
            @Value("${razor.key-id}") String razorPayKey,
            @Value("${razor.key-secret}") String razorPaySecret) {
        this.razorPayKey = razorPayKey;
        this.razorPaySecret = razorPaySecret;
    }
}
