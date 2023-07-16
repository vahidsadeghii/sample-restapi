package com.example.samplerest.handler.account;

import com.example.samplerest.domain.Account;
import com.example.samplerest.exception.AccountNotFoundException;
import com.example.samplerest.service.AccountService;
import com.example.samplerest.util.ResponseUtils;
import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class AccountHandler {
    private final AccountService accountService;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public AccountHandler(AccountService accountService) {
        this.accountService = accountService;
    }

    public void getAccount(RoutingContext routingContext) {
        String accountId = routingContext.pathParam("id");
        accountService.getAccountAsync(accountId)
                .onSuccess(
                        account -> ResponseUtils.buildSuccessResponseWithBody(
                                routingContext, toResponse(account)
                        )
                );
    }

    public void getAllAccounts(RoutingContext routingContext) {
         accountService.getAllAccountAsync()
                .onSuccess(
                        accounts -> ResponseUtils.buildSuccessResponseWithBody(
                                routingContext, accounts.stream().map(this::toResponse).collect(Collectors.toList())
                        )
                );
    }

    public AccountResponse toResponse(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .balance(account.getBalance())
                .depositPendingBalance(account.getDepositPendingBalance())
                .withdrawPendingBalance(account.getWithdrawPendingBalance())
                .createDate(account.getCreateDate().format(dateTimeFormatter))
                .updateDate(account.getUpdateDate() != null ?
                        account.getUpdateDate().format(dateTimeFormatter) : null)
                .build();
    }
}
