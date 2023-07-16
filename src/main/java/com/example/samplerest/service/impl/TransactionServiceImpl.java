package com.example.samplerest.service.impl;

import com.example.samplerest.domain.Account;
import com.example.samplerest.domain.TransactionInfo;
import com.example.samplerest.exception.BalanceNotEnoughException;
import com.example.samplerest.service.AccountService;
import com.example.samplerest.service.TransactionService;
import com.example.samplerest.service.WithdrawalService;
import io.vertx.core.Future;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TransactionServiceImpl implements TransactionService {
    private final AccountService accountService;
    private final WithdrawalService<Long> withdrawalService;
    private final ConcurrentMap<RequestId, TransactionInfo> requestIds = new ConcurrentHashMap<>(1000, 0.7f);

    public TransactionServiceImpl(AccountService accountService) {
        this.accountService = accountService;
        this.withdrawalService = new WithdrawalServiceDecorator<>();
    }

    private TransactionInfo createTransactionInfo(RequestId requestId, String fromAccountId, String toAccountId, long amount,
                                                  TransactionInfo.TransactionType transactionType) {
        TransactionInfo transactionInfo = TransactionInfo.builder()
                .id(System.nanoTime())
                .requestId(requestId.id())
                .fromAccount(fromAccountId)
                .toAccount(toAccountId)
                .createDate(LocalDateTime.now())
                .amount(amount)
                .transactionStatus(TransactionInfo.TransactionStatus.PROCESSING)
                .transactionType(transactionType)
                .build();

        var existTransactionInfo = requestIds.putIfAbsent(requestId, transactionInfo);
        if (null != existTransactionInfo &&
                existTransactionInfo.getAmount() == amount &&
                existTransactionInfo.getFromAccount().equals(fromAccountId) &&
                existTransactionInfo.getToAccount().equals(toAccountId)) {
            throw new RuntimeException("Duplicated request!");
        }

        return transactionInfo;
    }

    @Override
    public Future<TransactionInfo> internalTransfer(RequestId requestId, String fromAccountId, String toAccountId, long amount) {
        TransactionInfo transactionInfo = createTransactionInfo(requestId, fromAccountId, toAccountId, amount,
                TransactionInfo.TransactionType.INTERNAL);
        Account fromAccount = accountService.getAccount(fromAccountId).orElseThrow();
        Account toAccount = accountService.getAccount(toAccountId).orElseThrow();

        if (!fromAccount.decreaseBalance(amount)) {
            requestIds.remove(requestId);
            throw new BalanceNotEnoughException();

        } else {
            toAccount.increaseBalance(amount);

            transactionInfo.setTransactionStatus(TransactionInfo.TransactionStatus.COMPLETED);

            return Future.succeededFuture(transactionInfo);
        }
    }

    @Override
    public Future<TransactionInfo> transferToExternal(RequestId requestId, String fromAccountId, WithdrawalService.Address toWithdrawalAccountAddress, long amount) {
        TransactionInfo transactionInfo = createTransactionInfo(requestId, fromAccountId, toWithdrawalAccountAddress.value(), amount,
                TransactionInfo.TransactionType.TO_EXTERNAL);

        Account fromAccount = accountService.getAccount(fromAccountId).orElse(null);
        if (fromAccount == null || !fromAccount.addPendingWithdraw(amount)) {
            requestIds.remove(requestId);
            throw new BalanceNotEnoughException();

        } else {
            try {
                ((WithdrawalServiceDecorator<Long>) withdrawalService).requestDeposit(
                        new WithdrawalService.WithdrawalId(requestId.id()), toWithdrawalAccountAddress, amount
                );

            } catch (Exception e) {
                requestIds.remove(requestId);
                fromAccount.removePendingWithdraw(amount);
                throw e;
            }
        }

        return Future.succeededFuture(transactionInfo);
    }

    @Override
    public Future<TransactionInfo> transferFromExternal(RequestId requestId, WithdrawalService.Address fromWithdrawalAccountAddress, String toAccountId, long amount) {
        TransactionInfo transactionInfo = createTransactionInfo(requestId, fromWithdrawalAccountAddress.value(), toAccountId, amount,
                TransactionInfo.TransactionType.FROM_EXTERNAl);

        try {
            Account toAccount = accountService.getAccount(toAccountId).orElseThrow();

            withdrawalService.requestWithdrawal(
                    new WithdrawalService.WithdrawalId(requestId.id()), fromWithdrawalAccountAddress, amount
            );

            toAccount.addPendingDeposit(amount);

        } catch (RuntimeException e) {
            requestIds.remove(requestId);
            throw e;
        }

        return Future.succeededFuture(transactionInfo);
    }

    @Override
    public Future<TransactionInfo> getState(RequestId requestId) {
        TransactionInfo transactionInfo = requestIds.get(requestId);

        if (null == transactionInfo) {
            throw new RuntimeException("Transaction not found!");

        } else {
            if (transactionInfo.getTransactionStatus() == TransactionInfo.TransactionStatus.PROCESSING &&
                    transactionInfo.getTransactionType() != TransactionInfo.TransactionType.INTERNAL) {
                WithdrawalService.WithdrawalState requestState = withdrawalService.getRequestState(new WithdrawalService.WithdrawalId(requestId.id()));

                switch (requestState) {
                    case COMPLETED -> {
                        if (transactionInfo.getTransactionType() == TransactionInfo.TransactionType.TO_EXTERNAL) {
                            accountService.getAccount(transactionInfo.getFromAccount()).ifPresent(
                                    account -> account.releasePendingWithdraw(transactionInfo.getAmount())
                            );
                        } else {
                            accountService.getAccount(transactionInfo.getToAccount()).ifPresent(
                                    account -> account.releasePendingDeposit(transactionInfo.getAmount())
                            );
                        }
                        transactionInfo.setTransactionStatus(TransactionInfo.TransactionStatus.COMPLETED);
                    }
                    case FAILED -> {
                        if (transactionInfo.getTransactionType() == TransactionInfo.TransactionType.TO_EXTERNAL) {
                            accountService.getAccount(transactionInfo.getFromAccount()).ifPresent(
                                    account -> account.removePendingWithdraw(transactionInfo.getAmount())
                            );

                        } else {
                            accountService.getAccount(transactionInfo.getToAccount()).ifPresent(
                                    account -> account.removePendingDeposit(transactionInfo.getAmount())
                            );
                        }

                        transactionInfo.setTransactionStatus(TransactionInfo.TransactionStatus.FAILED);
                    }
                }
            }

            return Future.succeededFuture(transactionInfo);
        }
    }
}
