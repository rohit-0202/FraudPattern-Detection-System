package com.bank.frauddetection.dto;

import lombok.Data;

@Data
public class TransactionRequestDTO {
    private Long fromUserId;
    private Long toUserId;
    private double amount;
}
