package com.bank.frauddetection.service.impl;

import com.bank.frauddetection.entity.FraudLog;
import com.bank.frauddetection.entity.Transaction;
import com.bank.frauddetection.entity.User;
import com.bank.frauddetection.repository.FraudLogRepository;
import com.bank.frauddetection.repository.TransactionRepository;
import com.bank.frauddetection.repository.UserRepository;
import com.bank.frauddetection.service.AdminService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final FraudLogRepository fraudLogRepository;

    // ================= ADMIN VALIDATION =================
    private void validateAdmin(Long adminId) {

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Admin not found"
                ));

        if (!"ADMIN".equals(admin.getRole())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Access denied"
            );
        }

        if ("BLOCKED".equals(admin.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Admin is blocked"
            );
        }
    }

    // ================= GET USERS =================
    @Override
    public List<User> getAllUsers(Long adminId) {
        validateAdmin(adminId);
        return userRepository.findAll();
    }

    // ================= BLOCK USER =================
    @Override
    public String blockUser(Long adminId, Long userId) {
        validateAdmin(adminId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        user.setStatus("BLOCKED");
        userRepository.save(user);

        return "User blocked successfully";
    }

    // ================= UNBLOCK USER =================
    @Override
    public String unblockUser(Long adminId, Long userId) {
        validateAdmin(adminId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        user.setStatus("ACTIVE");

        // ❌ DO NOT reset risk score here
        userRepository.save(user);

        return "User unblocked successfully";
    }

    // ================= RESET RISK SCORE =================
    @Override
    public String resetRiskScore(Long adminId, Long userId) {
        validateAdmin(adminId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        user.setRiskScore(0);

        // Optional but sensible: ensure user is usable again
        if ("BLOCKED".equals(user.getStatus())) {
            user.setStatus("ACTIVE");
        }

        userRepository.save(user);

        return "Risk score reset successfully";
    }

    // ================= VIEW TRANSACTIONS =================
    @Override
    public List<Transaction> getAllTransactions(Long adminId) {
        validateAdmin(adminId);
        return transactionRepository.findAll();
    }

    // ================= VIEW FRAUD LOGS =================
    @Override
    public List<FraudLog> getFraudLogs(Long adminId) {
        validateAdmin(adminId);
        return fraudLogRepository.findAll();
    }
}
