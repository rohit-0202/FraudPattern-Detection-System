package com.bank.frauddetection.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bank.frauddetection.dto.TransactionRequestDTO;
import com.bank.frauddetection.dto.TransactionResponseDTO;
import com.bank.frauddetection.entity.Account;
import com.bank.frauddetection.entity.FraudLog;
import com.bank.frauddetection.entity.Transaction;
import com.bank.frauddetection.entity.User;
import com.bank.frauddetection.repository.AccountRepository;
import com.bank.frauddetection.repository.FraudLogRepository;
import com.bank.frauddetection.repository.TransactionRepository;
import com.bank.frauddetection.repository.UserRepository;
import com.bank.frauddetection.service.TransactionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final FraudLogRepository fraudLogRepository;

    private static final double HIGH_VALUE_LIMIT = 50000;
    private static final int RAPID_TX_THRESHOLD = 3; // 3 transfers
    private static final int RAPID_TX_WINDOW_MINUTES = 5;

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
        double transferredToday =
                transactionRepository.sumTransferredToday(
                        request.getFromUserId()
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

        // ================= RAPID TRANSACTION FREQUENCY =================
        LocalDateTime fiveMinutesAgo =
                LocalDateTime.now().minusMinutes(RAPID_TX_WINDOW_MINUTES);

        long recentTransfers =
                transactionRepository.countRecentTransfers(
                        request.getFromUserId(),
                        fiveMinutesAgo
                );

        if (recentTransfers >= RAPID_TX_THRESHOLD) {
            user.setRiskScore(user.getRiskScore() + 20);

            fraudLogRepository.save(
                    new FraudLog(
                            null,
                            user.getId(),
                            "Rapid transaction frequency detected (3 transfers in 5 minutes)",
                            user.getRiskScore(),
                            LocalDateTime.now()
                    )
            );
        }
        // ===============================================================

        // 🚨 High value fraud check
        if (request.getAmount() >= HIGH_VALUE_LIMIT) {
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

        // ❌ Block user if risk too high
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
