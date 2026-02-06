package com.bank.frauddetection.service;

import com.bank.frauddetection.entity.FraudLog;
import com.bank.frauddetection.entity.Transaction;
import com.bank.frauddetection.entity.User;

import java.util.List;

public interface AdminService {

    List<User> getAllUsers(Long adminId);

    String blockUser(Long adminId, Long userId);

    String unblockUser(Long adminId, Long userId);

    List<Transaction> getAllTransactions(Long adminId);

    List<FraudLog> getFraudLogs(Long adminId);
}
