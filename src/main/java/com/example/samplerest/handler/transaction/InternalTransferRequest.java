package com.example.samplerest.handler.transaction;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class InternalTransferRequest {
    private UUID requestId;
    private String fromAccountId;
    private String toAccountId;
    private long amount;
}
