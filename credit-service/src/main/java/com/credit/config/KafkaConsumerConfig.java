package com.credit.config;

import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<Object,Object> kafkaTemplate){
        DeadLetterPublishingRecoverer deadLetterPublishingRecoverer =
                new DeadLetterPublishingRecoverer(kafkaTemplate,(consumerRecord, exception) ->
                        new TopicPartition(consumerRecord.topic()+".DLT",consumerRecord.partition()));
        FixedBackOff fixedBackOff = new FixedBackOff(2000l,3);
        DefaultErrorHandler errorHandler =
                new DefaultErrorHandler(deadLetterPublishingRecoverer, fixedBackOff);
        // Don't retry validation errors
        errorHandler.addNotRetryableExceptions(IllegalArgumentException.class
        );
        return errorHandler;
    }


}
