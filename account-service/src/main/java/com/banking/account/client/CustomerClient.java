package com.banking.account.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "CUSTOMER-SERVICE",url = "http://localhost:8082/api/v1/")
public interface CustomerClient {

  @GetMapping("/customers/customer-id")
  String getCustomerId(@RequestParam("email") String email);
}
