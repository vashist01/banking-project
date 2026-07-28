package com.ai.context;


import com.ai.dto.ChatRequest;
import com.ai.dto.ToolContext;
import com.ai.planner.ExecutionPlan;

public interface ToolContextProvider {
    ToolContext getContext(ChatRequest request, ExecutionPlan plan);
}
