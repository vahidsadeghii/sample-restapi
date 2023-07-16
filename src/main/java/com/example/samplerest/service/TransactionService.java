package com.example.samplerest.service;

import com.example.samplerest.domain.TransactionInfo;
import io.vertx.core.Future;

import java.util.UUID;

public interface TransactionService {
    record RequestId(UUID id) {
    }

    Future<TransactionInfo> internalTransfer(RequestId requestId, String fromAccountId, String toAccountId, long amount);

    Future<TransactionInfo> transferToExternal(RequestId requestId, String fromAccountId, WithdrawalService.Address toWithdrawalAccountAddress,
                                               long amount);

    Future<TransactionInfo> transferFromExternal(RequestId requestId, WithdrawalService.Address fromWithdrawalAccountAddress,
                                                 String toAccountId,
                                                 long amount);

    Future<TransactionInfo> getState(RequestId requestId);
}
