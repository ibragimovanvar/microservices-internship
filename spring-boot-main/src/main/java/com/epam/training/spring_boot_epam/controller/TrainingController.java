package com.epam.training.spring_boot_epam.controller;

import com.epam.training.spring_boot_epam.dto.TrainerWorkloadResponseDTO;
import com.epam.training.spring_boot_epam.dto.TrainingDTO;
import com.epam.training.spring_boot_epam.dto.response.ApiResponse;
import com.epam.training.spring_boot_epam.service.TrainingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/trainings")
@RequiredArgsConstructor
public class TrainingController {

    private final TrainingService trainingService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createTraining(@Valid @RequestBody TrainingDTO dto){
        ApiResponse<Void> response = trainingService.addTraining(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{trainingId}")
    public ResponseEntity<ApiResponse<Void>> deleteTraining(@PathVariable Long trainingId){
        ApiResponse<Void> response = trainingService.cancelTraining(trainingId);
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }

    @GetMapping("/workload/monthly")
    public ResponseEntity<ApiResponse<TrainerWorkloadResponseDTO>> getTrainerWorkloadByMonth(@RequestParam("username") String username){
        return ResponseEntity.ok(trainingService.getTrainerWorkloadByMonth(username));
    }

}
