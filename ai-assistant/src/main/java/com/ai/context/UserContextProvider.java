package com.ai.context;


import com.ai.dto.ChatRequest;

public interface UserContextProvider {
    String getContext(ChatRequest request);
}
