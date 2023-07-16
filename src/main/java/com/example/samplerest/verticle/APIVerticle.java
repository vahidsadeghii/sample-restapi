package com.example.samplerest.verticle;

import com.example.samplerest.handler.account.AccountHandler;
import com.example.samplerest.handler.transaction.TransactionHandler;
import com.example.samplerest.router.AccountRouter;
import com.example.samplerest.router.TransactionRouter;
import com.example.samplerest.service.TransactionService;
import com.example.samplerest.service.impl.AccountServiceImpl;
import com.example.samplerest.service.impl.TransactionServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.jackson.DatabindCodec;
import io.vertx.ext.web.Router;

public class APIVerticle extends AbstractVerticle {

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        ObjectMapper mapper = DatabindCodec.mapper();
        mapper.registerModule(new JavaTimeModule());

        ObjectMapper prettyMapper = DatabindCodec.prettyMapper();
        prettyMapper.registerModule(new JavaTimeModule());


        AccountServiceImpl accountService = new AccountServiceImpl();
        TransactionService transactionService = new TransactionServiceImpl(accountService);

        TransactionHandler transactionHandler = new TransactionHandler(transactionService);

        Router router = Router.router(vertx);
        TransactionRouter transactionRouter = new TransactionRouter(vertx, transactionHandler);
        transactionRouter.buildRoutes(router);

        AccountRouter accountRouter = new AccountRouter(vertx, new AccountHandler(accountService));
        accountRouter.buildRoutes(router);

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(8080, result -> {
                    if (result.succeeded()) {
                        startPromise.complete();
                    } else {
                        startPromise.fail("Error");
                    }
                });
    }
}
