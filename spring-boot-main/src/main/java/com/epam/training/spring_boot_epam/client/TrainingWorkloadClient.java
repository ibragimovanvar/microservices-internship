package com.epam.training.spring_boot_epam.client;

import com.epam.training.spring_boot_epam.dto.TrainerWorkloadDTO;
import com.epam.training.spring_boot_epam.dto.TrainerWorkloadResponseDTO;
import com.epam.training.spring_boot_epam.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "TRAINING-WORKLOAD-SERVICE", path = "/api/v1/trainers-workload")
public interface TrainingWorkloadClient {

    @PostMapping
    ApiResponse<Void> acceptTrainerWorkload(@RequestHeader("Authorization") String token, @RequestBody TrainerWorkloadDTO trainerWorkloadDTO);

    @GetMapping("/by-month")
    ApiResponse<TrainerWorkloadResponseDTO> getTrainerWorkloadByMonth(@RequestHeader("Authorization") String token, @RequestParam("username") String username);

}