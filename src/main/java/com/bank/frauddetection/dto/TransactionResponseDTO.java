package com.bank.frauddetection.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionResponseDTO {
    private String message;
    private String status;
}
