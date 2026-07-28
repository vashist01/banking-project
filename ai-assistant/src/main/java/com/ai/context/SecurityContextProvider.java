package com.ai.context;


import com.ai.dto.ChatRequest;

public interface SecurityContextProvider {
    String getContext(ChatRequest request);
}
