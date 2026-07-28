package com.ai.intent;

public final class IntentPrompt {
    private IntentPrompt(){}

    public static String build(String question){

        return """
You are an intent classifier.

Classify the banking request.

Possible intents:

ACCOUNT_BALANCE
ACCOUNT_DETAILS
MINI_STATEMENT
MONEY_TRANSFER
BILL_PAYMENT
PAYMENT_HISTORY
LOAN_STATUS
EMI_DETAILS
LOAN_ELIGIBILITY
TRANSACTION_STATUS
FAQ
GENERAL_QUERY
UNKNOWN

Rules:

Return ONLY one intent.

Question:

%s
""".formatted(question);

    }

}
