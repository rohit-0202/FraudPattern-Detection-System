package com.bank.frauddetection.service.impl;

import com.bank.frauddetection.dto.*;
import com.bank.frauddetection.entity.Account;
import com.bank.frauddetection.entity.User;
import com.bank.frauddetection.repository.AccountRepository;
import com.bank.frauddetection.repository.UserRepository;
import com.bank.frauddetection.service.AuthService;
import com.bank.frauddetection.util.OtpUtil;

import jakarta.servlet.http.HttpServletRequest;
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
    private final PasswordEncoder passwordEncoder;

    // ================= REGISTER =================
    @Override
    public String register(RegisterRequestDTO request) {

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return "Username already exists";
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");
        user.setStatus("ACTIVE");
        user.setRiskScore(0);

        User savedUser = userRepository.save(user);

        Account account = new Account();
        account.setUserId(savedUser.getId());
        account.setBalance(0);
        account.setDailyLimit(10000);

        accountRepository.save(account);

        return "User registered successfully";
    }

    // ================= LOGIN =================
    @Override
    public LoginResponseDTO login(LoginRequestDTO request, HttpServletRequest httpRequest) {

        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());

        if (userOpt.isEmpty()) {
            return new LoginResponseDTO("Invalid username or password", null, null);
        }

        User user = userOpt.get();

        // WRONG PASSWORD
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {

            user.setRiskScore(user.getRiskScore() + 10);

            if (user.getRiskScore() >= 50) {
                user.setStatus("BLOCKED");
            }

            userRepository.save(user);
            return new LoginResponseDTO("Invalid username or password", null, null);
        }

        // BLOCKED USER
        if ("BLOCKED".equals(user.getStatus())) {
            return new LoginResponseDTO("User is blocked", null, null);
        }

        // ✅ SUCCESSFUL LOGIN → RESET RISK SCORE
        user.setRiskScore(0);
        userRepository.save(user);

        return new LoginResponseDTO(
                "Login successful",
                user.getId(),
                user.getRole()
        );
    }

    // ================= FORGOT PASSWORD =================
    @Override
    public String generateOtp(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String otp = OtpUtil.generateOtp();
        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));

        userRepository.save(user);

        return "OTP generated: " + otp;
    }

    // ================= RESET PASSWORD =================
    @Override
    public String resetPassword(String username, String otp, String newPassword) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getOtp() == null || !user.getOtp().equals(otp)) {
            return "Invalid OTP";
        }

        if (user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            return "OTP expired";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setOtp(null);
        user.setOtpExpiry(null);

        userRepository.save(user);

        return "Password updated successfully";
    }
}
