package com.ai.context;

import com.ai.dto.ChatRequest;
import com.ai.dto.MemoryContext;
import com.ai.dto.MemoryMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemoryContextProviderImpl
        implements MemoryContextProvider {
    private final ChatMemory chatMemory;
    @Override
    public MemoryContext getContext(ChatRequest request) {
        String conversationId = request.getUserId();
        List<Message> messagesList = chatMemory.get(conversationId);
        if(messagesList == null ||  messagesList.isEmpty()){
            return MemoryContext.builder()
                    .conversationId(conversationId)
                    .messages(List.of())
                    .summary("")
                    .totalMessages(0)
                    .build();
        }
       List<MemoryMessage> memoryMessages = messagesList.stream().
               map(message -> MemoryMessage.builder().role(message.getMessageType().name()).content(message.getText())
                       .timestamp(null)
               .build()).toList();
        String summary = memoryMessages.stream().map(memoryMessage -> memoryMessage.role()
        +": "+ memoryMessage.content()).collect(Collectors.joining(","));
        return MemoryContext.builder().summary(summary).conversationId(conversationId).messages(memoryMessages)
                .totalMessages(memoryMessages.size()).build();
    }
}