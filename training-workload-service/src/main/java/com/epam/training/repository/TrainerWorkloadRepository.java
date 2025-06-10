package com.epam.training.repository;

import com.epam.training.domain.TrainerWorkload;
import com.epam.training.domain.enums.Month;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrainerWorkloadRepository extends JpaRepository<TrainerWorkload, Long> {
    Optional<List<TrainerWorkload>> findByUsername(String username);
    Optional<TrainerWorkload> findByUsernameAndYearAndMonth(String username, int year, String month);
    boolean existsTrainerByUsername(String username);
}
