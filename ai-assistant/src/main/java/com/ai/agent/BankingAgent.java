package com.ai.agent;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BankingAgent {
    private final ChatClient chatClient;
    public String executePrompt(Prompt prompt) {
        return chatClient.prompt(prompt)
                .call()
                .content();
    }
}
