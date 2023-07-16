package com.example.samplerest.handler.transaction;

import com.example.samplerest.domain.TransactionInfo;
import com.example.samplerest.service.TransactionService;
import com.example.samplerest.service.WithdrawalService;
import com.example.samplerest.util.ResponseUtils;
import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;

import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class TransactionHandler {
    private final TransactionService transactionalService;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public TransactionHandler(TransactionService transactionalService) {
        this.transactionalService = transactionalService;
    }

    public void doInternalTransfer(RoutingContext routingContext) {
        //Todo: Handle input validation
        InternalTransferRequest request = routingContext.body().asPojo(InternalTransferRequest.class);

        transactionalService.internalTransfer(new TransactionService.RequestId(request.getRequestId()),
                        request.getFromAccountId(),
                        request.getToAccountId(), request.getAmount())
                .onSuccess(
                        result -> {
                            ResponseUtils.buildSuccessResponseWithBody(routingContext, toResponse(result));
                        }
                );
    }

    public void doTransferToWithdrawalAccount(RoutingContext routingContext) {
        TransferToWithdrawalAccountRequest request =
                routingContext.body().asPojo(TransferToWithdrawalAccountRequest.class);

        transactionalService.transferToExternal(new TransactionService.RequestId(request.getRequestId()),
                        request.getFromAccountId(),
                        new WithdrawalService.Address(request.getWithdrawalAddress()),
                        request.getAmount())
                .onSuccess(
                        result -> {
                            ResponseUtils.buildSuccessResponseWithBody(routingContext, toResponse(result));
                        }
                );
    }

    public void doTransferFromWithdrawalAccount(RoutingContext routingContext) {
        TransferFromWithdrawalAccountRequest request =
                routingContext.body().asPojo(TransferFromWithdrawalAccountRequest.class);

        transactionalService.transferFromExternal(new TransactionService.RequestId(request.getRequestId()),
                        new WithdrawalService.Address(request.getWithdrawalAddress()),
                        request.getToAccountId(),
                        request.getAmount())
                .onSuccess(
                        result -> {
                            ResponseUtils.buildSuccessResponseWithBody(routingContext, toResponse(result));
                        }
                );
    }

    public void getTransactionState(RoutingContext routingContext) {
        String requestId = routingContext.pathParam("requestId");

        //Todo: check requestId not null and empty
        transactionalService.getState(new TransactionService.RequestId(UUID.fromString(requestId)))
                .onSuccess(
                        result -> {
                            ResponseUtils.buildSuccessResponseWithBody(routingContext, toResponse(result));
                        }
                );
    }

    private TransactionResponse toResponse(TransactionInfo transactionInfo){
        return TransactionResponse.builder()
                .id(transactionInfo.getId() + "")
                .createDate(transactionInfo.getCreateDate().format(dateTimeFormatter))
                .amount(transactionInfo.getAmount())
                .requestId(transactionInfo.getRequestId().toString())
                .fromAccount(transactionInfo.getFromAccount())
                .toAccount(transactionInfo.getToAccount())
                .transactionType(transactionInfo.getTransactionType().name())
                .transactionStatus(transactionInfo.getTransactionStatus().name())
                .build();
    }
}
