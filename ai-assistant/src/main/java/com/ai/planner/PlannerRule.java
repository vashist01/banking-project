package com.ai.planner;

import com.ai.enums.ExecutionType;
import com.ai.enums.IntentType;

import java.util.EnumSet;
import java.util.Map;

public final class PlannerRule {

    private PlannerRule(){}

    public static final Map<IntentType, EnumSet<ExecutionType>> PLANNER_RULES =
            Map.of(IntentType.ACCOUNT_BALANCE, EnumSet.of(
                            ExecutionType.MCP,
                            ExecutionType.MEMORY,
                            ExecutionType.SECURITY
                    ),
                    IntentType.MONEY_TRANSFER,
                    EnumSet.of(
                            ExecutionType.MCP,
                            ExecutionType.MEMORY,
                            ExecutionType.SECURITY
                    ),
                    IntentType.MINI_STATEMENT,
                    EnumSet.of(
                            ExecutionType.MCP,
                            ExecutionType.SECURITY
                    ),
                    IntentType.FAQ,
                    EnumSet.of(
                            ExecutionType.RAG
                    ),
                    IntentType.LOAN_ELIGIBILITY,
                    EnumSet.of(
                            ExecutionType.MCP,
                            ExecutionType.RAG
                    ),
                    IntentType.UNKNOWN,
                    EnumSet.of(
                            ExecutionType.LLM_ONLY
                    ));
}
