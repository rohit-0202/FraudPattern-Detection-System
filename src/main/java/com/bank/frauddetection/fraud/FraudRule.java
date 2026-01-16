package com.bank.frauddetection.fraud;

import com.bank.frauddetection.entity.User;

public interface FraudRule {

    int apply(User user);
}