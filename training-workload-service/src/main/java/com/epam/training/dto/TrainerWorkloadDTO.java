package com.epam.training.dto;


import com.epam.training.domain.enums.Month;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
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
