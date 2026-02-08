package com.bank.frauddetection.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bank.frauddetection.dto.AccountSummaryDTO;
import com.bank.frauddetection.entity.Account;
import com.bank.frauddetection.service.AccountService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/account")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/balance/{userId}")
    public double getBalance(@PathVariable Long userId) {
        return accountService.getBalance(userId);
    }
  
    @PostMapping("/deposit")
    public String deposit(
            @RequestParam Long userId,
            @RequestParam double amount) {
        return accountService.deposit(userId, amount);
    }
    
    @GetMapping("/details/{userId}")
    public Account getAccount(@PathVariable Long userId) {
        return accountService.getAccount(userId);
    }

    @GetMapping("/summary/{userId}")
    public AccountSummaryDTO getAccountSummary(@PathVariable Long userId) {
        return accountService.getAccountSummary(userId);
    }

}
