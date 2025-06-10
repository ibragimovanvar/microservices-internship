package com.epam.training.dto;

import com.epam.training.domain.enums.Month;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyDataDTO {
    private String month;
    private Integer durationInMinutes;
    private BigDecimal durationInHours;
}
