package com.ai.dto;

import lombok.Builder;

import java.util.List;
@Builder
public record RagContext(
        String query,

        String context,

        int totalDocuments,

        List<RagDocument> documents
) {}