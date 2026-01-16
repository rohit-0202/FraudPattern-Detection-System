package com.bank.frauddetection.fraud;

import com.bank.frauddetection.entity.User;
import org.springframework.stereotype.Component;

@Component
public class HighAmountTransactionRule implements FraudRule {

    @Override
    public int apply(User user) {

        // simple static rule for now
        if (user.getRiskScore() > 50) {
            return 20;
        }
        return 0;
    }
}