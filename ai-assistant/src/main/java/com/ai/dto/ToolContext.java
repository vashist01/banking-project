package com.ai.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record ToolContext(

        List<ToolResult> results

) {
}