package com.epam.training.spring_boot_epam.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TrainerWorkloadResponseDTO {
    private String username;
    private String firstName;
    private String lastName;
    private boolean active;
    private Map<String, List<MonthlyDataDTO>> monthlyData;
}
