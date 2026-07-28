package com.ai.context;

import com.ai.dto.ChatRequest;
import org.springframework.stereotype.Service;

@Service
public class UserContextProviderImpl implements UserContextProvider{
    @Override
    public String getContext(ChatRequest request) {
        return "";
    }
}
