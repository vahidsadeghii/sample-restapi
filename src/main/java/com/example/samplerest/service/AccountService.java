package com.example.samplerest.service;

import com.example.samplerest.domain.Account;
import io.vertx.core.Future;

import java.util.List;
import java.util.Optional;

public interface AccountService {
    Optional<Account> getAccount(String id);
    Future<Account> getAccountAsync(String id);
    Future<List<Account>> getAllAccountAsync();
}
