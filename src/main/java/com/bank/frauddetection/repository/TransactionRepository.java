package com.bank.frauddetection.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bank.frauddetection.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByFromUserIdOrToUserId(Long fromUserId, Long toUserId);

    // ================= DAILY LIMIT (CUMULATIVE) =================
    @Query("""
        SELECT COALESCE(SUM(t.amount), 0)
        FROM Transaction t
        WHERE t.fromUserId = :userId
          AND t.type = 'TRANSFER'
          AND t.timestamp BETWEEN :start AND :end
    """)
    double sumTransferredToday(
            @Param("userId") Long userId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    // ================= RAPID TRANSACTION FREQUENCY =================
    @Query("""
        SELECT COUNT(t)
        FROM Transaction t
        WHERE t.fromUserId = :userId
          AND t.type = 'TRANSFER'
          AND t.timestamp >= :since
    """)
    long countRecentTransfers(
            @Param("userId") Long userId,
            @Param("since") LocalDateTime since
    );
}
