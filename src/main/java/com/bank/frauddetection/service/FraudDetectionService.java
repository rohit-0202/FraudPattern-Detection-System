package com.bank.frauddetection.service;

import com.bank.frauddetection.entity.User;

public interface FraudDetectionService {

    void detectFraud(User user);
}