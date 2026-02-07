package com.bank.frauddetection.service.impl;

import com.bank.frauddetection.entity.Account;
import com.bank.frauddetection.repository.AccountRepository;
import com.bank.frauddetection.service.AccountService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    public double getBalance(Long userId) {

        Account account = accountRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        return account.getBalance();
    }

    @Override
    public String deposit(Long userId, double amount) {

        if (amount <= 0) {
            return "Invalid amount";
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

}
