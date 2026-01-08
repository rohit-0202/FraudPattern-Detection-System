package com.bank.frauddetection.repository;

import com.bank.frauddetection.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}