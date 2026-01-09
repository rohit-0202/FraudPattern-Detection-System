package com.bank.frauddetection.service.impl;

import com.bank.frauddetection.dto.LoginRequestDTO;
import com.bank.frauddetection.dto.RegisterRequestDTO;
import com.bank.frauddetection.entity.Account;
import com.bank.frauddetection.entity.LoginLog;
import com.bank.frauddetection.entity.User;
import com.bank.frauddetection.repository.AccountRepository;
import com.bank.frauddetection.repository.LoginLogRepository;
import com.bank.frauddetection.repository.UserRepository;
import com.bank.frauddetection.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final LoginLogRepository loginLogRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public String register(RegisterRequestDTO request) {

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return "Username already exists";
        }

        // Save user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");
        user.setStatus("ACTIVE");
        user.setRiskScore(0);

        User savedUser = userRepository.save(user);

        // Create account with balance 0
        Account account = new Account();
        account.setUserId(savedUser.getId());
        account.setBalance(0.0);
        account.setDailyLimit(10000.0);

        accountRepository.save(account);

        return "User registered successfully";
    }

    @Override
    public String login(LoginRequestDTO request) {

        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());

        LoginLog log = new LoginLog();
        log.setLoginTime(LocalDateTime.now());

        if (userOpt.isEmpty()) {
            log.setSuccess(false);
            loginLogRepository.save(log);
            return "Invalid username or password";
        }

        User user = userOpt.get();
        log.setUserId(user.getId());

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.setSuccess(false);
            loginLogRepository.save(log);
            return "Invalid username or password";
        }

        if ("BLOCKED".equals(user.getStatus())) {
            log.setSuccess(false);
            loginLogRepository.save(log);
            return "User is blocked";
        }

        log.setSuccess(true);
        loginLogRepository.save(log);

        return "Login successful";
    }
}
