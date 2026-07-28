package com.banking.transaction.processer;

import com.banking.transaction.entity.TransactionOutBox;
import com.banking.transaction.enums.TransactionStatusEnum;
import com.banking.transaction.publisher.TransactionKafkaPublisher;
import com.banking.transaction.repository.TransactionOutBoxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProcessTransactionEvent {
    private final TransactionOutBoxRepository transactionOutBoxRepository;
    private final TransactionKafkaPublisher transactionKafkaPublisher;

    @Scheduled(fixedDelayString = "${outbox.transaction.event.scheduler.delay}")
    @SchedulerLock(name = "processingTransactionInitiate",
            lockAtMostFor = "PT5M", //lockAtMostFor = "PT5M" → If the application crashes, the lock is automatically released after 5
            lockAtLeastFor = "PT5S" // lockAtLeastFor = "PT5S" → Keep the lock for at least 5 seconds,
            // preventing another instance from immediately running the same job.
    )
    public void processingTransactionInitiate(){
        List<TransactionOutBox> transactionList = transactionOutBoxRepository
                .findAllByTransactionStatus(TransactionStatusEnum.PROCESSING.name());
        if(CollectionUtils.isEmpty(transactionList)){
            log.info("No Pending Transaction to process.");
            return;
        }
        for(TransactionOutBox transactionOutBox: transactionList){
            try {
                transactionKafkaPublisher.initiateTransactionEvent(transactionOutBox).get();
            }catch (Exception exception){
                log.error("Error: Failed to initiate Transaction and balance is deducted :{}",exception);
            }
        }
    }
}
