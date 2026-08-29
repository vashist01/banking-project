package com.dashboard.service;

import com.dashboard.exception.DashboardServiceException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import java.util.concurrent.TimeUnit;
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

    private final AccountClient  accountClient;
    private final TransactionClient transactionClient;

    private final Executor dashboardExecutor;

    public DashboardResponse getDashboard() {
        CompletableFuture<List<AccountResponse>> account = CompletableFuture.supplyAsync(
            accountClient::getAccountDashboardDetail,dashboardExecutor);
         
        CompletableFuture<List<TransactionResponse>> transaction = CompletableFuture.supplyAsync(
            transactionClient::getAllTransanctions,dashboardExecutor);
  try{
    return account.thenCombine(transaction,
        DashboardResponse::new).orTimeout(3, TimeUnit.SECONDS).join();
  } catch (Exception ex) {
    log.error("Failed to build dashboard", ex);

    throw new DashboardServiceException(
        "Unable to fetch dashboard details",
        ex.getCause()
    );
  }



    }
}
