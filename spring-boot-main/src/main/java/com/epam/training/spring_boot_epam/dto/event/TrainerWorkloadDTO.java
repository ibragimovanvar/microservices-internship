package com.epam.training.spring_boot_epam.dto.event;


import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrainerWorkloadDTO {

    @NotBlank(message = "Trainer username cannot be empty")
    private String username;

    @NotBlank(message = "Trainer first name cannot be empty")
    private String firstName;

    private String lastName;

    @NotBlank(message = "Action type cannot be empty")
    private String actionType;

    private boolean active;

    private int year;

    private String month;

    private int minutes;
}
