package com.banking.notification.consumer;

import com.banking.notification.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
@Component
@Slf4j
public class EventListener {
    private NotificationService notificationService;
    @KafkaListener(topics = "transaction.varification.otp")
    public void consumeOtpGenerateEvent(@Payload Map<String,Object> payload){
        try{
            String accountNumber = payload.get("accountNumber").toString();
            String otp = payload.get("otp").toString();
            String transactionId = payload.get("transactionId").toString();
            BigDecimal amount = (BigDecimal) payload.get("amount");
            String reason = (String) payload.get("reason");
            sendEmailAlert(accountNumber,"TRANSACTION VARIFICATION REQUIRED",
                    String.format("Suspicious activity is deducted on your account. "
                            +"Reason: %s"+
                            "A Transaction of %s is pending verification "+
                            "Your OTP is: %s. Valid for 5 minute." +
                            "If This wasn't you -ignore this message."));

        }catch (Exception e){
            log.error("Error: Sending otp verification Notification: {}",e.getMessage());
        }

    }

    private void sendEmailAlert(String accountNumber, String subject, String message) {
        log.info(" --------------------------------------- ");
        log.info("Account Number: {}",accountNumber);
        log.info("Subject: {}",subject);
        log.info("Message: {}",message);

    }

    @KafkaListener(topics = "transaction.completed")
    public void consumeTransactionComplete(@Payload Map<String,Object> paylaod){
        String senderAccountNumber = paylaod.get("senderAccountNumber").toString();
        String receiverAccountNumber = paylaod.get("receiverAccountNumber").toString();
        String amount = paylaod.get("amount").toString();
        sendEmailAlert(senderAccountNumber,"DEBIT ALERT",
                String.format("%s debit from account %s",amount,senderAccountNumber));
        sendEmailAlert(receiverAccountNumber,"CREDIT ALERT",
                String.format("%s credit from account %s",amount,receiverAccountNumber));

    }

    @KafkaListener(topics = "fraud.deducted")
    public void fraudDeducted(@Payload Map<String,Object> payload){
        try{
            String accountNumber = payload.get("accountNumber").toString();
            String reason = payload.get("reason").toString();

            sendEmailAlert(accountNumber,"SUSPICIOUS ACTIVITY DEDUCTED",
                    String.format("Your account  %s has been blocked debit from account" +
                            "Reason %s" +
                            "Please contact your back immediately",accountNumber,reason));
        } catch (Exception e) {
            log.error("Error: fraud deduction notification.",e.getMessage());
        }


    }


    @KafkaListener(topics = "transaction.refund")
    public void consumeRefunded(@Payload Map<String,Object> payload){
        try{
            String senderAccountNumber = payload.get("senderAccountNumber").toString();
            String amount = payload.get("amount").toString();
            String reason = payload.get("reason").toString();
            sendEmailAlert(senderAccountNumber,"REFUND PROCESSED",
                    String.format("Your Transaction of %s was cancelled." +
                            "Reason: %s" +
                            "%s has been refunded to account %s",amount,reason,amount,senderAccountNumber));
        } catch (Exception e) {
            log.error("Error: Sending Refund notification.",e.getMessage());
        }
    }

    @KafkaListener(topics = "payment.completed")
    public void consumePaymentCompleted(@Payload Map<String,Object> payload){
        try{
            String accountNumber = payload.get("accountNumber").toString();
            String amount = payload.get("amount").toString();
            sendEmailAlert(accountNumber,"Payment successfully",String.format("Payment of %s completed." +
                    "Razorpay ID: %s",amount,payload.get("razorpayPaymentId")));
        }catch (Exception exception){
            log.error("Error: Sending payment notification.",exception.getMessage());
        }
    }
    @KafkaListener(topics = "payment.failed")
    public void consumePaymentFailed(@Payload Map<String,Object> payload){
        try{
            String accountNumber = payload.get("accountNumber").toString();
            String amount = payload.get("amount").toString();
            sendEmailAlert(accountNumber,"Payment Failed",String.format("Payment of %s failed." +
                    "Razorpay ID: %s",amount,payload.get("razorpayPaymentId")));
        }catch (Exception exception){
            log.error("Error: Sending payment failed notification.",exception.getMessage());
        }
    }

    @KafkaListener(topics = "create-account-event")
    public void consumeCreateAccountEvent(@Payload Map<String,Object> payload){
        try{
            notificationService.sendNotificationAlert(payload);
        }catch (Exception exception){

        }
    }
}
