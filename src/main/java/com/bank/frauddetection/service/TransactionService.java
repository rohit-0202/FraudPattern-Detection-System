package com.bank.frauddetection.service;

import com.bank.frauddetection.dto.TransactionRequestDTO;
import com.bank.frauddetection.dto.TransactionResponseDTO;
import com.bank.frauddetection.entity.Transaction;

import java.util.List;

public interface TransactionService {

    TransactionResponseDTO transferMoney(TransactionRequestDTO request);

    List<Transaction> getUserTransactions(Long userId);
}
