package com.epam.training.spring_boot_epam.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyDataDTO {
    private String month;
    private Integer durationInMinutes;
    private BigDecimal durationInHours;
}
