package com.bank.frauddetection.fraud;

import com.bank.frauddetection.entity.LoginLog;
import com.bank.frauddetection.entity.User;
import com.bank.frauddetection.repository.LoginLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MultipleLoginFailureRule implements FraudRule {

    private final LoginLogRepository loginLogRepository;

    @Override
    public int apply(User user) {

        List<LoginLog> logs = loginLogRepository.findAll();

        long failedAttempts = logs.stream()
                .filter(log -> log.getUserId().equals(user.getId()))
                .filter(log -> !log.isSuccess())
                .count();

        if (failedAttempts >= 3) {
            return 30; // risk points
        }
        return 0;
    }
}