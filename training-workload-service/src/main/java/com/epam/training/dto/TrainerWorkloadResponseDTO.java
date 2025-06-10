package com.epam.training.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.Month;
import java.util.List;
import java.util.Map;


@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TrainerWorkloadResponseDTO {
    private String username;
    private String firstName;
    private String lastName;
    private boolean active;
    private Map<String, List<MonthlyDataDTO>> monthlyData;
}
