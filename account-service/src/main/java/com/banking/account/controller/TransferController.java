package com.banking.account.controller;

import com.banking.account.dto.request.BulkTransferRequest;
import com.banking.account.dto.response.AccountBalanceResponse;
import com.banking.account.dto.response.BaseResponse;
import com.banking.account.dto.response.TransferResponse;
import com.banking.account.service.AccountTransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
@Slf4j
public class TransferController {
    private final AccountTransferService accountTransferService;
    @PostMapping("/transfer")
    public BaseResponse<TransferResponse> transfer(
            @RequestParam("sender_account_number") String senderAccountNumber,
            @RequestParam("receiver_account_number") String receiverAccountNumber,
            @RequestParam BigDecimal amount,
            @RequestParam String reference) {

        log.info("Received transfer request: senderAccountNumber={}, receiverAccountNumber={}, amount={}, reference={}",
                senderAccountNumber, receiverAccountNumber, amount, reference);

        TransferResponse response = accountTransferService.transfer( senderAccountNumber,
                receiverAccountNumber, amount,reference
        );

        return BaseResponse.success(
                response, String.format("Transfer completed successfully. Amount: %s %s",
                        amount, response.getCurrency())
        );
    }

    /**
     * POST /api/v1/accounts/transfer/bulk - Bulk transfer
     * Transfers money from one account to multiple accounts
     */
    @PostMapping("/transfer/bulk") 
    public BaseResponse<List<TransferResponse>> bulkTransfer(
            @Valid @RequestBody BulkTransferRequest request) {

        log.info("Received bulk transfer request from: {}", request.getSourceId());

        List<TransferResponse> responses = accountTransferService.bulkTransfer(request);

        return BaseResponse.success(
                responses,
                "Bulk transfer completed successfully. Total transfers: " + responses.size()
        );
    }

    /**
     * POST /api/v1/accounts/{id}/hold - Hold amount
     * Places a hold on an amount for pending transactions
     */
    @PostMapping("/{id}/hold")
    @ResponseStatus(HttpStatus.OK)

    public BaseResponse<AccountBalanceResponse> holdAmount(
            @PathVariable String id,
            @RequestParam BigDecimal amount,
            @RequestParam String reference) {

        log.info("Holding amount on account: {}, amount: {}, reference: {}",
                id, amount, reference);

        AccountBalanceResponse response = accountTransferService.holdAmount(
                id,
                amount,
                reference
        );

        return BaseResponse.success(
                response,
                "Amount held successfully. Held amount: " + amount
        );
    }

    /**
     * POST /api/v1/accounts/{id}/release-hold - Release hold
     * Releases a previously placed hold
     */
    @PostMapping("/{id}/release-hold")
    public BaseResponse<AccountBalanceResponse> releaseHold(
            @PathVariable String id,
            @RequestParam BigDecimal amount,
            @RequestParam String reference) {

        log.info("Releasing hold on account: {}, amount: {}, reference: {}",
                id, amount, reference);

        AccountBalanceResponse response = accountTransferService.releaseHold(
                id,
                amount,
                reference
        );

        return BaseResponse.success(
                response,
                "Hold released successfully. Released amount: " + amount
        );
    }
}
