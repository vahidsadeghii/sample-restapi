package com.example.samplerest.service.impl;

import com.example.samplerest.service.WithdrawalService;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class WithdrawalServiceDecorator<T> implements WithdrawalService<T> {
    private final WithdrawalServiceImpl<T> withdrawalService = new WithdrawalServiceImpl<>();
    private final ConcurrentMap<WithdrawalId, WithdrawalServiceImpl.Withdrawal<T>> depositRequests = new ConcurrentHashMap<>();

    public void requestDeposit(WithdrawalId id, Address address, T amount) { // Please substitute T with prefered type
        final var existing = depositRequests.putIfAbsent(id, new WithdrawalServiceImpl.Withdrawal<>(
                WithdrawalState.COMPLETED, 0, address, amount));

        if (existing != null && !Objects.equals(existing.address(), address) && !Objects.equals(existing.amount(), amount))
            throw new IllegalStateException("Deposit request with id[%s] is already present".formatted(id));
    }

    @Override
    public void requestWithdrawal(WithdrawalId id, Address address, T amount) {
        //Todo: How to check balance?
        withdrawalService.requestWithdrawal(id, address, amount);
    }

    @Override
    public WithdrawalState getRequestState(WithdrawalId id) {
        try {
            return withdrawalService.getRequestState(id);
        } catch (IllegalArgumentException e) {
            final var withdrawal = depositRequests.get(id);

            if (withdrawal != null) {
                return withdrawal.finalState();

            } else {
                throw e;
            }
        }
    }
}
