package com.example.samplerest.handler.transaction;

import com.example.samplerest.domain.TransactionInfo;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class TransactionResponse {
    private String id;
    private String requestId;
    private String fromAccount;
    private String toAccount;
    private String createDate;
    private long amount;
    private String transactionStatus;
    private String transactionType;
}
