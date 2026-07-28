package com.ai.service;

import com.ai.dto.ChatRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ToolExecuteServiceImpl implements ToolExecuteService{
    private final ToolExecutionAgent toolExecutionAgent;
    @Override
    public String execute(ChatRequest request) {
        return toolExecutionAgent.execute(request);
    }
}
