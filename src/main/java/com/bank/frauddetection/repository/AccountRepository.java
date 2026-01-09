package com.bank.frauddetection.repository;

import com.bank.frauddetection.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
