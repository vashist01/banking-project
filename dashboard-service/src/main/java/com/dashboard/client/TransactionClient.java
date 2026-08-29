package com.dashboard.client;


import com.dashboard.dto.TransactionResponse;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "TRANSACTION-SERVICE",url = "https://localhost:8008/api/v1/transaction")
public interface TransactionClient {

  @GetMapping("/all-transactions")
  public List<TransactionResponse> getAllTransanctions();


}
