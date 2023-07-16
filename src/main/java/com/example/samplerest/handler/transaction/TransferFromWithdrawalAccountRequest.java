package com.example.samplerest.handler.transaction;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class TransferFromWithdrawalAccountRequest {
    private UUID requestId;
    private String withdrawalAddress;
    private String toAccountId;
    private long amount;
}
