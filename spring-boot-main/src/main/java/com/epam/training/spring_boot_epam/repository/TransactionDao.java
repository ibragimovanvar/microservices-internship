package com.epam.training.spring_boot_epam.repository;

import com.epam.training.spring_boot_epam.domain.TransactionLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionDao extends JpaRepository<TransactionLog, String> {

}
