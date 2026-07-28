package com.ai.context;

import com.ai.dto.ChatRequest;
import com.ai.dto.MemoryContext;

public interface MemoryContextProvider {


    MemoryContext getContext(ChatRequest request);
}