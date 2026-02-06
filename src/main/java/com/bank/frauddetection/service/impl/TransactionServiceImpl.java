package com.bank.frauddetection.service.impl;

import com.bank.frauddetection.dto.TransactionRequestDTO;
import com.bank.frauddetection.dto.TransactionResponseDTO;
import com.bank.frauddetection.entity.Account;
import com.bank.frauddetection.entity.Transaction;
import com.bank.frauddetection.repository.AccountRepository;
import com.bank.frauddetection.repository.TransactionRepository;
import com.bank.frauddetection.service.TransactionService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public TransactionResponseDTO transferMoney(TransactionRequestDTO request) {

        Account sender = accountRepository.findByUserId(request.getFromUserId())
                .orElse(null);

        Account receiver = accountRepository.findByUserId(request.getToUserId())
                .orElse(null);

        if (sender == null || receiver == null) {
            return new TransactionResponseDTO(
                    "Invalid sender or receiver",
                    "FAILED"
            );
        }

        if (request.getAmount() <= 0) {
            return new TransactionResponseDTO(
                    "Invalid amount",
                    "FAILED"
            );
        }

        if (sender.getBalance() < request.getAmount()) {
            return new TransactionResponseDTO(
                    "Insufficient balance",
                    "FAILED"
            );
        }

        // Perform transfer
        sender.setBalance(sender.getBalance() - request.getAmount());
        receiver.setBalance(receiver.getBalance() + request.getAmount());

        accountRepository.save(sender);
        accountRepository.save(receiver);

        // Save transaction
        Transaction tx = new Transaction();
        tx.setFromUserId(request.getFromUserId());
        tx.setToUserId(request.getToUserId());
        tx.setAmount(request.getAmount());
        tx.setType("TRANSFER");
        tx.setTimestamp(LocalDateTime.now());

        transactionRepository.save(tx);

        return new TransactionResponseDTO(
                "Transfer successful",
                "SUCCESS"
        );
    }
}
