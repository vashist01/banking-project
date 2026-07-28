package com.ai.dto;

import lombok.Builder;

import java.util.Map;

@Builder
public record RagDocument(

        String content,

        String fileName,

        Integer page,

        Double score,

        Map<String, Object> metadata

) {
}