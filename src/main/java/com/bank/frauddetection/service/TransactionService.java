package com.bank.frauddetection.service;

import com.bank.frauddetection.dto.TransactionRequestDTO;
import com.bank.frauddetection.dto.TransactionResponseDTO;

public interface TransactionService {

    TransactionResponseDTO transferMoney(TransactionRequestDTO request);
}