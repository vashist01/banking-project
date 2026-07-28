package com.ai.planner;

import com.ai.enums.ExecutionType;
import com.ai.enums.IntentType;
import lombok.Builder;
import lombok.Data;

import java.util.EnumSet;

@Data
@Builder
public class ExecutionPlan {
    private IntentType intentType;
    private EnumSet<ExecutionType> executionTypes;

}
