package com.ai.context;

import com.ai.dto.ChatRequest;
import org.springframework.stereotype.Service;

@Service
public class SecurityContextProviderImpl implements SecurityContextProvider{
    @Override
    public String getContext(ChatRequest request) {
        return "";
    }
}
