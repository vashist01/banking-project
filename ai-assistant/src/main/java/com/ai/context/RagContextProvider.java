package com.ai.context;

import com.ai.dto.ChatRequest;
import com.ai.dto.RagContext;

public interface RagContextProvider {
    public RagContext getContext(ChatRequest chatRequest);
}
