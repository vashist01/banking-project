package com.ai.context;

import com.ai.dto.ChatRequest;
import com.ai.planner.ExecutionPlan;

public interface ContextAggregator {
    ConversationContext gather(ExecutionPlan executionPlan, ChatRequest request);
}
