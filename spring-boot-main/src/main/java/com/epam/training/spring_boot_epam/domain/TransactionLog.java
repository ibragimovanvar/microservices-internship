package com.epam.training.spring_boot_epam.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "transaction_log")
@Getter
@Setter
public class TransactionLog {
    @Id
    private String transactionId;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String endpoint = "/";

    @Column(nullable = false)
    private String httpMethod;

    @Column(nullable = false)
    private boolean success;

    private int responseStatus;

    private LocalDateTime timestamp;
}
