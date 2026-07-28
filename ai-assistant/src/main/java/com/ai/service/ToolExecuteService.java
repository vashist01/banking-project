package com.ai.service;

import com.ai.dto.ChatRequest;

public interface ToolExecuteService {
    String execute(ChatRequest request);
}
