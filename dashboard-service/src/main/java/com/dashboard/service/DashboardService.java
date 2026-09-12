package com.dashboard.service;

import com.dashboard.exception.DashboardServiceException;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.bulkhead.annotation.Bulkhead.Type;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import java.util.concurrent.TimeUnit;

import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Service;

import com.dashboard.client.AccountClient;
import com.dashboard.client.TransactionClient;
import com.dashboard.dto.AccountResponse;
import com.dashboard.dto.DashboardResponse;
import com.dashboard.dto.TransactionResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * DashboardService
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final AccountClient accountClient;
    private final TransactionClient transactionClient;

    private final Executor dashboardExecutor;

    @CircuitBreaker(name = "dasboard-service", fallbackMethod = "accountFallback")
    @Retry(name = "account-service")
    @Bulkhead(name = "dasboard-service")
    public DashboardResponse getDashboard() {
        CompletableFuture<List<AccountResponse>> account = CompletableFuture.supplyAsync(
                accountClient::getAccountDashboardDetail, dashboardExecutor);

        CompletableFuture<List<TransactionResponse>> transaction = CompletableFuture.supplyAsync(
                transactionClient::getAllTransanctions, dashboardExecutor).orTimeout(3, TimeUnit.SECONDS);
        try {
            return account.thenCombine(transaction,
                    DashboardResponse::new).join();
        } catch (Exception ex) {
            log.error("Failed to build dashboard", ex);

            throw new DashboardServiceException(
                    "Unable to fetch dashboard details",
                    ex.getCause());
        }

    }

     private CompletableFuture<List<AccountResponse>> accountFallback(
            Throwable throwable) {

        return CompletableFuture.completedFuture(
            Collections.emptyList()
        );
    }
}
