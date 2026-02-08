package com.bank.frauddetection.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountSummaryDTO {

    private double balance;
    private double dailyLimit;
    private double transferredToday;
    private double remainingDailyLimit;
}
