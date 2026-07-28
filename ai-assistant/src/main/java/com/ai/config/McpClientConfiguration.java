package com.ai.config;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class McpClientConfiguration {
    // the use of toolbackprovider is to inform the LLM (OpenAI, Gemini, Claude) how many tool is available in
    // application
    public ToolCallbackProvider toolCallbackProvider(List<ToolCallback> toolCallbackList){
        //When the Spring Boot application starts, the Spring IoC container automatically scan every bean of type
        // ToolCallback in the Application Context and injects them as a List<ToolCallback> into the dependent bean.
        return ToolCallbackProvider.from(toolCallbackList);
    }

}
