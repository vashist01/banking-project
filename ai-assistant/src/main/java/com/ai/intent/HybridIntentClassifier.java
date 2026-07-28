package com.ai.intent;

import com.ai.dto.IntentResult;
import com.ai.enums.IntentType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HybridIntentClassifier {
    private final RuleBasedIntentClassifier ruleClassifier;
    private final IntentClassifier intentClassifier;
    public IntentResult classify(String message) {
        Optional<IntentType> ruleIntent = intentClassifier.intentTypeClassify(message);
        if(ruleIntent.isEmpty()){
            return IntentResult.builder()
                    .intent(ruleIntent.get())
                    .confidence(1.0)
                    .build();
        }
        IntentResult intentResult = intentClassifier.classify(message);
        return intentResult;
    }
}
