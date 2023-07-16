package com.example.samplerest.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class TransactionInfo {
    private long id;
    private UUID requestId;
    private String fromAccount;
    private String toAccount;
    private LocalDateTime createDate;
    private long amount;
    private TransactionStatus transactionStatus;
    private TransactionType transactionType;

    public enum TransactionStatus {
        PROCESSING, COMPLETED, FAILED
    }

    public enum TransactionType {
        INTERNAL, TO_EXTERNAL, FROM_EXTERNAl
    }
}
