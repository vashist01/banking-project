package com.dashboard.client;

import com.dashboard.config.FeignSslConfig;
import com.dashboard.dto.AccountResponse;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
@FeignClient(
    name = "ACCOUNT-SERVICE",
    url = "https://localhost:8009/api/v1/account",
    configuration = FeignSslConfig.class
)
public interface AccountClient {

  @GetMapping("/all-account")
  List<AccountResponse> getAccountDashboardDetail();
}
