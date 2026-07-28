package com.ai.planner;

import com.ai.dto.IntentResult;
import com.ai.enums.ExecutionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.EnumSet;

@Service
@Slf4j
public class ExecutionPlanner {
    public  ExecutionPlan createPlan(IntentResult intentResult){
        EnumSet<ExecutionType> executionTypes = PlannerRule.PLANNER_RULES.getOrDefault(intentResult.getIntent(),EnumSet.of(ExecutionType.LLM_ONLY));

        ExecutionPlan plan = ExecutionPlan.builder()

                .intentType(intentResult.getIntent())

                .executionTypes(executionTypes)

                .build();

        log.info("Execution Plan : {}", plan);
        return plan;
    }
}
