package com.epam.training.controller;

import com.epam.training.dto.TrainerWorkloadDTO;
import com.epam.training.dto.TrainerWorkloadResponseDTO;
import com.epam.training.dto.response.ApiResponse;
import com.epam.training.service.TrainerWorkloadService;
import com.epam.training.util.DomainUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/trainers-workload")
@RequiredArgsConstructor
public class TrainerWorkloadController {

    private final TrainerWorkloadService trainerWorkloadService;
    private final DomainUtils domainUtils;

    @GetMapping("/by-month")
    public ResponseEntity<ApiResponse<TrainerWorkloadResponseDTO>> getTrainerWorkloadByMonth(@RequestParam("username") String username) {
        domainUtils.checkUsername(username);

        return new ResponseEntity<>(trainerWorkloadService.getTrainersWorkload(username), HttpStatus.OK);
    }
}
