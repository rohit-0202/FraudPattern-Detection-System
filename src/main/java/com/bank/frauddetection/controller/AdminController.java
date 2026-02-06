package com.bank.frauddetection.controller;

import com.bank.frauddetection.entity.FraudLog;
import com.bank.frauddetection.entity.Transaction;
import com.bank.frauddetection.entity.User;
import com.bank.frauddetection.service.AdminService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // ✅ GET ALL USERS
    @GetMapping("/users")
    public List<User> getAllUsers(@RequestParam Long adminId) {
        return adminService.getAllUsers(adminId);
    }

    // ✅ BLOCK USER
    @PostMapping("/block/{userId}")
    public String blockUser(
            @RequestParam Long adminId,
            @PathVariable Long userId) {
        return adminService.blockUser(adminId, userId);
    }

    // ✅ UNBLOCK USER
    @PostMapping("/unblock/{userId}")
    public String unblockUser(
            @RequestParam Long adminId,
            @PathVariable Long userId) {
        return adminService.unblockUser(adminId, userId);
    }

    // ✅ VIEW ALL TRANSACTIONS
    @GetMapping("/transactions")
    public List<Transaction> getAllTransactions(@RequestParam Long adminId) {
        return adminService.getAllTransactions(adminId);
    }

    // ✅ VIEW FRAUD LOGS
    @GetMapping("/fraud-logs")
    public List<FraudLog> getFraudLogs(@RequestParam Long adminId) {
        return adminService.getFraudLogs(adminId);
    }
}
