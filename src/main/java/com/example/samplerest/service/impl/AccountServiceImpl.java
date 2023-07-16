package com.example.samplerest.service.impl;

import com.example.samplerest.domain.Account;
import com.example.samplerest.service.AccountService;
import io.vertx.core.Future;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

public class AccountServiceImpl implements AccountService {
    private final Map<String, Account> accounts;

    public AccountServiceImpl() {
        accounts = new HashMap<>();
        IntStream.range(1, 10).forEach(
                id -> accounts.put(id + "",
                        Account.builder()
                                .id(id + "")
                                .createDate(LocalDateTime.now())
                                .balance(10000)
                                .build())
        );
    }

    @Override
    public Optional<Account> getAccount(String id) {
        return Optional.ofNullable(accounts.get(id));
    }

    @Override
    public Future<Account> getAccountAsync(String id) {
        return Future.succeededFuture(
                accounts.get(id)
        );
    }

    @Override
    public Future<List<Account>> getAllAccountAsync() {
        return Future.succeededFuture(
                accounts.values().stream().toList()
        );
    }
}
