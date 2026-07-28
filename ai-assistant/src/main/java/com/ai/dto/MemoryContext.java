package com.ai.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record MemoryContext(
    String conversationId,
    List<MemoryMessage> messages,
    String summary,
    int totalMessages
) {}