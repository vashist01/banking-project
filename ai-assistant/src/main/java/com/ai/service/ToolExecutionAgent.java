package com.ai.service;

import com.ai.dto.ChatRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ToolExecutionAgent {

    private final ChatClient toolChatClient;

    public String execute(ChatRequest request) {

        return toolChatClient.prompt()
                .user(request.getMessage())
                .call()
                .content();
    }
}