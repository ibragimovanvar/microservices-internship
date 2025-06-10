package com.epam.training.service;

import com.epam.training.dto.TrainerWorkloadDTO;
import com.epam.training.dto.TrainerWorkloadResponseDTO;
import com.epam.training.dto.response.ApiResponse;

public interface TrainerWorkloadService {
    ApiResponse<Void> acceptTrainerWorkload(TrainerWorkloadDTO trainerWorkloadDTO);
    ApiResponse<TrainerWorkloadResponseDTO> getTrainersWorkload(String username);
}