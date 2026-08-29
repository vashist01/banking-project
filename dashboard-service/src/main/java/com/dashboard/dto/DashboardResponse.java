package com.dashboard.dto;

import java.util.List;

/**
 * DashboardResponse
 */
public record DashboardResponse(List<AccountResponse> accountResponse,
                                List<TransactionResponse> transactionResponse) {
}
