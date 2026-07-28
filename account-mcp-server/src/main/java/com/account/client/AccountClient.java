package com.account.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountClient {
    private final WebClient accountWebClient;
    public String getAccountBalance(String userAccountNumber) {
        return accountWebClient.get().uri("http://localhost:8007/api/v1/accounts/{accountNumber}/balance",
                userAccountNumber).retrieve().bodyToMono(String.class)
                .block();

    }
}
