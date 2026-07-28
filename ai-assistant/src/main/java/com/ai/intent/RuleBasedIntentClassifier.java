package com.ai.intent;

import com.ai.enums.IntentType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RuleBasedIntentClassifier {
    public Optional<IntentType> classify(String question){
        String text = question.toLowerCase();
        if (text.contains("balance")) {
            return Optional.of(IntentType.ACCOUNT_BALANCE);
        }

        if (text.contains("transfer")) {
            return Optional.of(IntentType.MONEY_TRANSFER);
        }

        if (text.contains("mini statement")) {
            return Optional.of(IntentType.MINI_STATEMENT);
        }

        if (text.contains("bill payment")) {
            return Optional.of(IntentType.BILL_PAYMENT);
        }

        if (text.contains("loan status")) {
            return Optional.of(IntentType.LOAN_STATUS);
        }

        return Optional.empty();
    }
}