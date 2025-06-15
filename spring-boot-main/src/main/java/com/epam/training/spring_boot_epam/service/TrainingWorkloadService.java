package com.epam.training.spring_boot_epam.service;

import com.epam.training.spring_boot_epam.dto.TrainerWorkloadDTO;
import com.epam.training.spring_boot_epam.dto.TrainerWorkloadResponseDTO;
import com.epam.training.spring_boot_epam.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface TrainingWorkloadService {
    ResponseEntity<ApiResponse<TrainerWorkloadResponseDTO>> getTrainerWorkloadByMonth(String username);
    ResponseEntity<ApiResponse<TrainerWorkloadResponseDTO>> fallbackGetByMonth(String username, Throwable t);
}
