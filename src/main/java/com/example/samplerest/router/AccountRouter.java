package com.example.samplerest.router;

import com.example.samplerest.handler.account.AccountHandler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

public class AccountRouter {
    private final Vertx vertx;
    private final AccountHandler accountHandler;

    public AccountRouter(Vertx vertx, AccountHandler accountHandler) {
        this.vertx = vertx;
        this.accountHandler = accountHandler;
    }

    public void buildRoutes(Router router) {
        router.get("/accounts").handler(
                accountHandler::getAllAccounts
        );

        router.get("/accounts/:id").handler(
                accountHandler::getAccount
        );
    }
}
