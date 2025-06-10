package com.epam.training.spring_boot_epam.service.impl;

import com.epam.training.spring_boot_epam.domain.TransactionLog;
import com.epam.training.spring_boot_epam.repository.TransactionDao;
import com.epam.training.spring_boot_epam.service.TransactionLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionLogServiceImpl implements TransactionLogService {
    private final TransactionDao repository;

    @Override
    public void save(TransactionLog log) {
        repository.save(log);
    }
}
