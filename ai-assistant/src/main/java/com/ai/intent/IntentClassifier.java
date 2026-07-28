package com.ai.intent;

import com.ai.dto.IntentResult;
import com.ai.enums.IntentType;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class IntentClassifier {
    private final ChatClient chatClient;

    public IntentResult classify(String question){

        String response = getResponse(question);
        IntentType intentType;
        try{
            intentType = IntentType.valueOf(response.trim().toUpperCase(Locale.ROOT));
        }catch (Exception exception){
            intentType = IntentType.UNKNOWN;

        }
        return IntentResult.builder().intent(intentType).confidence(0.98).build();
    }

    private String getResponse(String question) {
        return   chatClient.prompt().user(IntentPrompt.build(question)).call().content();
    }

    public Optional<IntentType> intentTypeClassify(String message) {
        String response = getResponse(message);
        return Optional.of(IntentType.valueOf(response));
    }
}
