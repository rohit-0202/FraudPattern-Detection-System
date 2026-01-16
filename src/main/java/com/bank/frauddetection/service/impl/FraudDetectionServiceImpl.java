package com.bank.frauddetection.service.impl;

import com.bank.frauddetection.entity.FraudLog;
import com.bank.frauddetection.entity.User;
import com.bank.frauddetection.fraud.RiskScoreCalculator;
import com.bank.frauddetection.repository.FraudLogRepository;
import com.bank.frauddetection.repository.UserRepository;
import com.bank.frauddetection.service.FraudDetectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FraudDetectionServiceImpl implements FraudDetectionService {

    private final RiskScoreCalculator riskScoreCalculator;
    private final FraudLogRepository fraudLogRepository;
    private final UserRepository userRepository;

    @Override
    public void detectFraud(User user) {

        int riskScore = riskScoreCalculator.calculate(user);

        user.setRiskScore(riskScore);
        userRepository.save(user);

        if (riskScore >= 50) {

            FraudLog fraudLog = new FraudLog();
            fraudLog.setUserId(user.getId());
            fraudLog.setRiskScore(riskScore);
            fraudLog.setReason("Suspicious activity detected");
            fraudLog.setCreatedAt(LocalDateTime.now());

            fraudLogRepository.save(fraudLog);
        }
    }
}