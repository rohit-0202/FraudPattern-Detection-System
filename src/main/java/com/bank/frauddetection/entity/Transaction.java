package com.bank.frauddetection.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long fromUserId;
    private Long toUserId;

    private double amount;

    private String type;   // TRANSFER / DEPOSIT

    private String status; // SUCCESS / FAILED

    private LocalDateTime timestamp;
}
