package com.banking.account.scheduler;

import com.banking.account.entity.OutBoxPattern;
import com.banking.account.repository.OutBoxPatternRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentProcesser {
    private static final String PAYMENT_COMPLETE_TOPIC = "payment.completed";
    private static final String PAYMENT_FAILED_TOPIC = "payment.failed";
    private final OutBoxPatternRepository outBoxRepository;
    private final KafkaTemplate<String,Object> kafkaTemplate;

    @Scheduled(fixedDelayString = "${outbox.scheduler.delay}")
//    @SchedulerLock(name = "processCompletePayment",
//            lockAtMostFor = "PT5M", //lockAtMostFor = "PT5M" → If the application crashes, the lock is automatically released after 5
//            lockAtLeastFor = "PT5S" // lockAtLeastFor = "PT5S" → Keep the lock for at least 5 seconds, preventing another instance from immediately running the same job.
//     )
    public void processCompletePayment(){
        try {

            List<OutBoxPattern> outBoxPattern = outBoxRepository.findByStatus("PROCESSING");
            if(outBoxPattern.isEmpty()){
                return;
            }
            outBoxPattern.forEach(outBoxPattern1 -> {
             CompletableFuture<SendResult<String,Object>> completableFuture =
                 kafkaTemplate.send(PAYMENT_COMPLETE_TOPIC,outBoxPattern1.getTransactionId(),outBoxPattern);
                 completableFuture.whenComplete((result,exception) ->{
                    if(exception == null){
                      outBoxPattern1.setEventStatus("EVENT-SENT");
                      outBoxRepository.save(outBoxPattern1);
                    }else{
                      log.error("Failed to publish message", exception);
                    }
                 });
            });
            }catch (Exception exception){
                log.error("Failed: failed to process payment complete event by process",exception.getMessage());
        }
    }
}
