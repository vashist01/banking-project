package com.ai.context;

import com.ai.dto.ChatRequest;
import com.ai.dto.MemoryContext;
import com.ai.dto.RagContext;
import com.ai.dto.ToolContext;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationContext {

    private String userQuestion;

    private RagContext ragContext;

    private MemoryContext memoryContext;

    private ToolContext toolContext;

    private String userContext;

    private String securityContext;

    private ChatRequest request;
}