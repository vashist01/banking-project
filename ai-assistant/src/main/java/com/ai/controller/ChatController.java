package com.ai.controller;

import com.ai.dto.ChatRequest;
import com.ai.dto.ChatResponse;
import com.ai.service.ConversationOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ConversationOrchestrator orchestrator;

    @PostMapping
    public ChatResponse chat(
            @RequestBody @Validated ChatRequest request){
        return orchestrator.process(request);

    }

}