package com.banking.payment.service;

import com.banking.payment.config.PropertyConfiguration;
import com.banking.payment.dto.CreatePaymentRequest;
import com.banking.payment.dto.response.PaymentOrderResponse;
import com.banking.payment.entity.OutBoxPattern;
import com.banking.payment.entity.Payment;
import com.banking.payment.enums.PaymentStatus;
import com.banking.payment.pattern.PaymentStrategy;
import com.banking.payment.repository.OutBoxRepository;
import com.banking.payment.repository.PaymentRepository;
import com.banking.payment.scheduler.PaymentProcesser;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OutBoxRepository outBoxRepository;
    private final PaymentProcesser paymentProcesser;
    private final Map<String,PaymentStrategy> payStartegyMap ;
   private final PropertyConfiguration propertyConfiguration;


    /**
     * FLOW:
     * 1 create order in razorpay
     * 2. save payment record in DB
     * 3. return order details to frontend
     * 4. Frontend show razorpay checkout
     * 5.user pays
     * 6.Razorpay call webhook
     *
     * @param createPaymentRequest
     * @return
     */
    @Transactional
    public  PaymentOrderResponse createPaymentOrder(@Valid CreatePaymentRequest createPaymentRequest) {
        log.info("Creating payment order for account:{} amount: {}",createPaymentRequest.getAccountNumber()
                ,createPaymentRequest.getAmount());
    try{
        RazorpayClient razorpayClient = new RazorpayClient(propertyConfiguration.getRazorPayKey(),propertyConfiguration.getRazorPaySecret());
        // convert amount
        int convertAmount = createPaymentRequest.getAmount().multiply(BigDecimal.valueOf(100)).intValue();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("amount",convertAmount);
        jsonObject.put("currency","USD");
        jsonObject.put("receipt","rcpt_"+System.currentTimeMillis()+ UUID.randomUUID().toString()
                .replace("-","").substring(0,10));
        Order order = razorpayClient.orders.create(jsonObject);
        String razorpayOrderId = order.get("id");
         // 1. Create entity
        Payment payment = Payment.builder()
                .accountNumber(createPaymentRequest.getAccountNumber()).razorpayOrderId(razorpayOrderId)
                .amount(createPaymentRequest.getAmount()).paymentStatus(PaymentStatus.CREATED)
                .paymentType(payStartegyMap.get(createPaymentRequest.getPaymentType()))
                .description(Optional.ofNullable(createPaymentRequest.getDescription()).orElse("")).build();

 // 2. Find strategy
 

    Payment payment1 = paymentRepository.save(payment);
    return mapToPaymentOrderResponse(payment1,razorpayOrderId);
    } catch (RazorpayException e) {
        throw new RuntimeException(e);
    }
    }

    private PaymentOrderResponse mapToPaymentOrderResponse(Payment payment1, String razorpayOrderId) {
        return PaymentOrderResponse.builder().paymentId(String.valueOf(payment1.getId()))
                .razorpayOrderId(razorpayOrderId).currency(payment1.getCurrency())
                .amount(payment1.getAmount()).status(String.valueOf(payment1.getPaymentStatus()))
                .razorpayKeyId(propertyConfiguration.getRazorPayKey()).build();
    }


    public void handlePaymentWebhook(HashMap<String, Object> webhookRequest) {
        log.info("handling payment webhook event:{}",webhookRequest);

        String event = webhookRequest.get("event").toString();
        if("payment.captured".equals(event)){
            handleSuccessPayment(webhookRequest);
        }else if("payment.failed".equals(event)){
            handleFailedPayment(webhookRequest);
        }

    }

    private void handleFailedPayment(HashMap<String, Object> webhookRequest) {
        Payment payment = getPaymentData(webhookRequest,PaymentStatus.FAILED );
        payment.setFailedReason("Payment failed via Razorpay");
        paymentRepository.save(payment);
        paymentProcesser.sendPaymentFailedEvent(payment);
    }

    private void handleSuccessPayment(HashMap<String, Object> webhookRequest) {
        Payment payment = getPaymentData(webhookRequest,PaymentStatus.COMPLETED);
        paymentRepository.save(payment);
        // implement here outbox pattern here
        OutBoxPattern outBoxPattern = new OutBoxPattern();
        outBoxPattern.setPaymentId(String.valueOf(payment.getId()));
        outBoxPattern.setRazorPaymentId(payment.getRazorpayPaymentId());
        outBoxPattern.setAmount(payment.getAmount());
        outBoxPattern.setAccountNumber(payment.getAccountNumber());
        outBoxPattern.setStatus("PROCESSING");
        outBoxRepository.save(outBoxPattern);

    }

    private Payment getPaymentData(HashMap<String, Object> webhookRequest, PaymentStatus paymentStatus) {
        Map<String,Object> paymentData  = extractWebData(webhookRequest);
        String orderId = paymentData.get("order_id").toString();
        String paymentId = paymentData.get("id").toString();
        // check idempotence here by redis and aquire lock here
        // then check with idempotence table based order_id

        Payment payment = paymentRepository.findByRazorpayOrderId(orderId).orElseThrow(()->
                new RuntimeException("Payment not Found for order"+orderId));
        payment.setRazorpayPaymentId(paymentId);
        payment.setPaymentStatus(paymentStatus);
        return payment;

    }

    private Map<String, Object> extractWebData(HashMap<String, Object> webhookRequest) {
        Map<String,Object>   entity = (Map<String, Object>) webhookRequest.get("payload");
        Map<String,Object> paymentWrapper = (Map<String, Object>) entity.get("payment");
        return (Map<String, Object>) paymentWrapper.get("entity");
    }
}
