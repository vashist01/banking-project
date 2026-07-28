package com.ai.service;

import com.ai.context.ConversationContext;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PromptBuilder {

    public Prompt build(ConversationContext context) {

        String systemPrompt = """
                You are a secure Banking AI Assistant.
                Use the provided context to answer accurately.
                """;

        String userPrompt = """
                Security Context:
                %s

                Memory Context:
                %s

                Tool Context:
                %s

                RAG Context:
                %s

                User Question:
                %s
                """.formatted(
                context.getSecurityContext(),
                context.getMemoryContext(),
                context.getToolContext(),
                context.getRagContext(),
                context.getRequest().getMessage());

        return new Prompt(
                List.of(
                        new SystemMessage(systemPrompt),
                        new UserMessage(userPrompt)
                )
        );
    }
}