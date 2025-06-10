package com.epam.training.spring_boot_epam.service;

import com.epam.training.spring_boot_epam.domain.TransactionLog;

public interface TransactionLogService {
    void save(TransactionLog log);
}
