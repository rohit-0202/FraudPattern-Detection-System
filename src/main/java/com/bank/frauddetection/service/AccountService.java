package com.bank.frauddetection.service;

import com.bank.frauddetection.entity.Account;

public interface AccountService {

    double getBalance(Long userId);

    String deposit(Long userId, double amount);
    
    Account getAccount(Long userId);

}
