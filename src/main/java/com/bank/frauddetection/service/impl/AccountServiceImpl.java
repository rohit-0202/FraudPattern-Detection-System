package com.bank.frauddetection.service.impl;

import com.bank.frauddetection.dto.AccountSummaryDTO;
import com.bank.frauddetection.entity.Account;
import com.bank.frauddetection.entity.User;
import com.bank.frauddetection.repository.AccountRepository;
import com.bank.frauddetection.repository.TransactionRepository;
import com.bank.frauddetection.repository.UserRepository;
import com.bank.frauddetection.service.AccountService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public double getBalance(Long userId) {
        Account account = accountRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (account.getBalance() < 0) {
            account.setBalance(0);
            accountRepository.save(account);
        }

        return account.getBalance();
    }

    @Override
    public String deposit(Long userId, double amount) {

        if (amount <= 0) {
            return "Deposit amount must be greater than 0";
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if ("BLOCKED".equals(user.getStatus())) {
            return "Account is blocked";
        }

        Account account = accountRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        account.setBalance(account.getBalance() + amount);
        accountRepository.save(account);

        return "Deposit successful";
    }

    @Override
    public Account getAccount(Long userId) {
        return accountRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    // ===============================
    // ACCOUNT SUMMARY (NEW)
    // ===============================
    @Override
    public AccountSummaryDTO getAccountSummary(Long userId) {

        Account account = accountRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime now = LocalDateTime.now();

        double transferredToday =
                transactionRepository.sumTransferredToday(
                        userId,
                        startOfDay,
                        now
                );

        double remainingDailyLimit =
                Math.max(0, account.getDailyLimit() - transferredToday);

        return new AccountSummaryDTO(
                account.getBalance(),
                account.getDailyLimit(),
                transferredToday,
                remainingDailyLimit
        );
    }
}
