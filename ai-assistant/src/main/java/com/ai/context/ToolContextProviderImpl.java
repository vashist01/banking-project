package com.ai.context;

import com.ai.dto.ChatRequest;
import com.ai.dto.ToolContext;
import com.ai.planner.ExecutionPlan;
import com.ai.service.ToolExecuteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ToolContextProviderImpl implements ToolContextProvider{
    private final ToolExecuteService toolExecuteService;
    @Override
    public ToolContext getContext(ChatRequest request, ExecutionPlan plan) {
        String response = toolExecuteService.execute(request);
        return ToolContext.builder().build();
    }
}
