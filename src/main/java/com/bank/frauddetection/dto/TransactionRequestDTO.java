package com.bank.frauddetection.dto;

import lombok.Data;

@Data
public class TransactionRequestDTO {

    private Long fromAccount;
    private Long toAccount;
    private double amount;
}