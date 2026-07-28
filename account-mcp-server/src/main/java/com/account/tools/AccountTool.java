package com.account.tools;


import com.account.client.AccountClient;
import com.account.dto.AccountBalanceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountTool {

    private final AccountClient accountClient;

    @Tool(name = "getAccountBalance", // unique identification of tool llm will understand what user want
    description = "Return Account balance using accountNumber and userId") // llm is not understanding the java code
    // llm will understand the descprition and call the tool what user need
    public AccountBalanceResponse getAccountBalance(String userId, String userAccountNumber){
        String response = accountClient.getAccountBalance(userAccountNumber);
        return AccountBalanceResponse.builder().accountNumber(userAccountNumber).
                availableBalance(BigDecimal.valueOf(Long.parseLong(response))).build();
    }
    @Tool(
            name = "getAllCustomerAccount",
            description = "Returns all customer accounts"
    )
    public List<AccountDto> getAllCustomerAccount(String customerId){
        return List.of(AccountDto.builder().build());

    }
}
