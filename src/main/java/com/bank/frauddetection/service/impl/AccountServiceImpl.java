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

        Account account = accountRepository.findAll()
                .stream()
                .filter(a -> a.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Account not found"));

        return account.getBalance();
    }
}