package com.bank.frauddetection.controller;

import com.bank.frauddetection.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/balance/{userId}")
    public double getBalance(@PathVariable Long userId) {
        return accountService.getBalance(userId);
    }
}