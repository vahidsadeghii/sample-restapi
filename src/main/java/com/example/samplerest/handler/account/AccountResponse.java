package com.example.samplerest.handler.account;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AccountResponse {
    private String id;
    private String createDate;
    private long balance;
    private long withdrawPendingBalance;
    private long depositPendingBalance;
    private String updateDate;
}
