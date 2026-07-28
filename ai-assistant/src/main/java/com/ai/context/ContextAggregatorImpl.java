package com.ai.context;


import com.ai.dto.ChatRequest;
import com.ai.dto.MemoryContext;
import com.ai.dto.RagContext;
import com.ai.dto.ToolContext;
import com.ai.enums.ExecutionType;
import com.ai.planner.ExecutionPlan;
import jakarta.ws.rs.core.SecurityContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContextAggregatorImpl implements ContextAggregator{
    private final RagContextProvider ragProvider;

    private final MemoryContextProvider memoryProvider;

    private final ToolContextProvider toolProvider;

    private final UserContextProvider userProvider;

    private final SecurityContextProvider securityProvider;



    @Override
    public ConversationContext gather(ExecutionPlan plan, ChatRequest request) {
        MemoryContext memory = null;

        RagContext rag = null;

        ToolContext tool = null;

        SecurityContext security = null;

        if(plan.getExecutionTypes() .contains(ExecutionType.MEMORY)){
          memory =memoryProvider.getContext(request);
        }

        if(plan.getExecutionTypes().contains(ExecutionType.RAG)){
            rag =  ragProvider.getContext(request);
        }

        if(plan.getExecutionTypes().contains(ExecutionType.MCP)){
            tool =  toolProvider.getContext(request,plan);
        }

        if(plan.getExecutionTypes() .contains(ExecutionType.SECURITY)){

          //  security = securityProvider.getContext(request);

        }

        return ConversationContext.builder()

                .request(request)

                .memoryContext(memory)

                .ragContext(rag)

                .toolContext(tool)

               // .securityContext(security)

                .build();

    }
}
