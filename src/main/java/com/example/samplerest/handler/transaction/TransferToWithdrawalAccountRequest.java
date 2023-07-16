package com.example.samplerest.handler.transaction;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class TransferToWithdrawalAccountRequest {
    private UUID requestId;
    private String fromAccountId;
    private String withdrawalAddress;
    private long amount;
}
