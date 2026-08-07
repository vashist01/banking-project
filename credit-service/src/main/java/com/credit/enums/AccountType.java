package com.credit.enums;

public enum AccountType {
    SAVINGS,
    CURRENT,
    CHECKING,
    FIXED_DEPOSIT ,
    BUSINESS,
    LOAN,
    CREDIT_CARD
    ;

    public static AccountType getAccountType(String accountType){
        if(accountType == SAVINGS.name()){
            return SAVINGS;
        }if(accountType == CURRENT.name()){
            return CURRENT;
        }else{
            return FIXED_DEPOSIT;
        }

    }
}
