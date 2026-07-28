package com.banking.account.repository.projection;

import java.math.BigDecimal;

public interface DailyTransferLimitProjection {
    BigDecimal getDailyTransferLimit();
}
