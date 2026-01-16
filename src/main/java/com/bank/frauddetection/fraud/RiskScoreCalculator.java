package com.bank.frauddetection.fraud;

import com.bank.frauddetection.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RiskScoreCalculator {

    private final List<FraudRule> fraudRules;

    public int calculate(User user) {

        int totalScore = 0;

        for (FraudRule rule : fraudRules) {
            totalScore += rule.apply(user);
        }

        return totalScore;
    }
}