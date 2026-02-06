package com.bank.frauddetection.service;

public interface AccountService {

    double getBalance(Long userId);

    String deposit(Long userId, double amount);
}
