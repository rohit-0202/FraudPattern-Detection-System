package com.bank.frauddetection.service.impl;

import com.bank.frauddetection.dto.TransactionRequestDTO;
import com.bank.frauddetection.dto.TransactionResponseDTO;
import com.bank.frauddetection.entity.Account;
import com.bank.frauddetection.entity.Transaction;
import com.bank.frauddetection.entity.User;
import com.bank.frauddetection.repository.AccountRepository;
import com.bank.frauddetection.repository.TransactionRepository;
import com.bank.frauddetection.repository.UserRepository;
import com.bank.frauddetection.service.FraudDetectionService;
import com.bank.frauddetection.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final FraudDetectionService fraudDetectionService;

    @Override
    public TransactionResponseDTO transferMoney(TransactionRequestDTO request) {

        Account fromAccount = accountRepository.findById(request.getFromAccount())
                .orElseThrow(() -> new RuntimeException("Sender account not found"));

        Account toAccount = accountRepository.findById(request.getToAccount())
                .orElseThrow(() -> new RuntimeException("Receiver account not found"));

        if (fromAccount.getBalance() < request.getAmount()) {
            return new TransactionResponseDTO("FAILED", "Insufficient balance");
        }

        // Debit sender
        fromAccount.setBalance(fromAccount.getBalance() - request.getAmount());

        // Credit receiver
        toAccount.setBalance(toAccount.getBalance() + request.getAmount());

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        Transaction transaction = new Transaction();
        transaction.setFromAccount(fromAccount.getId());
        transaction.setToAccount(toAccount.getId());
        transaction.setAmount(request.getAmount());
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setStatus("SUCCESS");

        transactionRepository.save(transaction);

        // ================================
        // STEP 7 – FRAUD DETECTION TRIGGER
        // ================================
        User user = userRepository.findById(fromAccount.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        fraudDetectionService.detectFraud(user);

        return new TransactionResponseDTO("SUCCESS", "Transaction completed");
    }
}