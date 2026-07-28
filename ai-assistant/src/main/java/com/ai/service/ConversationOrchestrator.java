package com.ai.service;

import com.ai.agent.BankingAgent;
import com.ai.context.ContextAggregator;
import com.ai.context.ConversationContext;
import com.ai.dto.ChatRequest;
import com.ai.dto.ChatResponse;
import com.ai.dto.IntentResult;
import com.ai.intent.HybridIntentClassifier;
import com.ai.planner.ExecutionPlan;
import com.ai.planner.ExecutionPlanner;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConversationOrchestrator {

    private final HybridIntentClassifier hybridIntentClassifier;
    private final ExecutionPlanner executionPlanner;
    private final ContextAggregator contextAggregator;
    private final PromptBuilder promptBuilder;
    private final BankingAgent bankingAgent;

    public ChatResponse process(ChatRequest request){
        IntentResult intentResult = hybridIntentClassifier.classify(request.getMessage()); //It identifies what the user wants.
        ExecutionPlan plan = executionPlanner.createPlan(intentResult); //It decides how to fulfill the user's request. What does the user want? How should the system satisfy it?
        ConversationContext conversationContext =contextAggregator.gather(plan,request); //It collects all information needed before calling the AI model.
        Prompt prompt = promptBuilder.build(conversationContext); //Converts the structured context into a prompt that the LLM can understand.
        // LLMs understand prompts, not Java objects.
        String response = bankingAgent.executePrompt(prompt);
        return ChatResponse.builder()
                .answer(plan.toString())
                .build();
    }
}