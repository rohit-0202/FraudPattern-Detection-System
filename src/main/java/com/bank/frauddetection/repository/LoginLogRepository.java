package com.bank.frauddetection.repository;

import com.bank.frauddetection.entity.LoginLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginLogRepository extends JpaRepository<LoginLog, Long> {
}