package com.bank.frauddetection.service.impl;

import com.bank.frauddetection.dto.TransactionRequestDTO;
import com.bank.frauddetection.dto.TransactionResponseDTO;
import com.bank.frauddetection.entity.*;
import com.bank.frauddetection.repository.*;
import com.bank.frauddetection.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final FraudLogRepository fraudLogRepository;

    private static final double HIGH_VALUE_LIMIT = 50000;

    // ===============================
    // TRANSFER MONEY
    // ===============================
    @Override
    public TransactionResponseDTO transferMoney(TransactionRequestDTO request) {

        // ❌ Invalid amount
        if (request.getAmount() <= 0) {
            return new TransactionResponseDTO(
                    "Transfer amount must be greater than 0",
                    "FAILED"
            );
        }

        Account sender = accountRepository.findByUserId(request.getFromUserId())
                .orElse(null);

        Account receiver = accountRepository.findByUserId(request.getToUserId())
                .orElse(null);

        if (sender == null || receiver == null) {
            return new TransactionResponseDTO("Invalid sender or receiver", "FAILED");
        }

        // ❌ Exceeds balance
        if (request.getAmount() > sender.getBalance()) {
            return new TransactionResponseDTO("Transfer amount exceeds balance", "FAILED");
        }

        // ================= DAILY LIMIT (CUMULATIVE) =================
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDateTime.now();

        double transferredToday =
                transactionRepository.sumTransferredToday(
                        request.getFromUserId(),
                        startOfDay,
                        endOfDay
                );

        if (transferredToday + request.getAmount() > sender.getDailyLimit()) {
            return new TransactionResponseDTO(
                    "Daily transfer limit exceeded",
                    "FAILED"
            );
        }
        // ============================================================

        User user = userRepository.findById(request.getFromUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 🚨 High value fraud check
        if (request.getAmount() > HIGH_VALUE_LIMIT) {
            user.setRiskScore(user.getRiskScore() + 30);

            fraudLogRepository.save(
                    new FraudLog(
                            null,
                            user.getId(),
                            "High value transaction",
                            user.getRiskScore(),
                            LocalDateTime.now()
                    )
            );
        }

        if (user.getRiskScore() >= 50) {
            user.setStatus("BLOCKED");
            userRepository.save(user);
            return new TransactionResponseDTO(
                    "User blocked due to fraud",
                    "FAILED"
            );
        }

        userRepository.save(user);

        // ✅ Perform transfer
        sender.setBalance(sender.getBalance() - request.getAmount());
        receiver.setBalance(receiver.getBalance() + request.getAmount());

        accountRepository.save(sender);
        accountRepository.save(receiver);

        Transaction tx = new Transaction();
        tx.setFromUserId(request.getFromUserId());
        tx.setToUserId(request.getToUserId());
        tx.setAmount(request.getAmount());
        tx.setType("TRANSFER");
        tx.setStatus("SUCCESS");
        tx.setTimestamp(LocalDateTime.now());

        transactionRepository.save(tx);

        return new TransactionResponseDTO("Transfer successful", "SUCCESS");
    }

    // ===============================
    // GET USER TRANSACTIONS
    // ===============================
    @Override
    public List<Transaction> getUserTransactions(Long userId) {
        return transactionRepository.findByFromUserIdOrToUserId(userId, userId);
    }
}
