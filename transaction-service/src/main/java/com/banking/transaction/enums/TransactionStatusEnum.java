package com.banking.transaction.enums;

/**
 *  Transaction lifecycle flow
 *  PENDING -> PROCESSING-> COMPLETED(clean transaction no fraud)
 *      -> PENDING_VERIFICATION(suspicious detected)
 *     -> COMPLETED(verified)
 *     -> FLAGGED( saga refund)
 *     -FAILED
 *    -> FLAGGED
 */
public enum TransactionStatusEnum {
    PENDING,
    PROCESSING,
    COMPLETED,
    PENDING_VERIFICATION,
    FAILED,
    FLAGGED
}
