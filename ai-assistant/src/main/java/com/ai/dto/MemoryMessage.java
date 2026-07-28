package com.ai.dto;

import lombok.Builder;

@Builder
public record MemoryMessage(

        String role,

        String content,

        String timestamp

) {
}