package com.example.samplerest.router;

import com.example.samplerest.handler.transaction.TransactionHandler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class TransactionRouter {
    private final Vertx vertx;
    private final TransactionHandler transactionHandler;

    public TransactionRouter(Vertx vertx, TransactionHandler transactionHandler) {
        this.vertx = vertx;
        this.transactionHandler = transactionHandler;
    }

    public void buildRoutes(Router router) {
        router.post("/transactions/transfers/internal").handler(
                BodyHandler.create()
        ).handler(
                transactionHandler::doInternalTransfer
        );

        router.post("/transactions/transfers/to-external").handler(
                BodyHandler.create()
        ).handler(
                transactionHandler::doTransferToWithdrawalAccount
        );

        router.post("/transactions/transfers/from-external").handler(
                BodyHandler.create()
        ).handler(
                transactionHandler::doTransferFromWithdrawalAccount
        );

        router.get("/transactions/:requestId").handler(
                transactionHandler::getTransactionState
        );
    }
}
