package com.epam.training.domain;

import com.epam.training.domain.enums.Month;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "trainer_workload")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TrainerWorkload {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String firstName;

    private String lastName;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "workload_year", nullable = false)
    private int year;

    @Column(name = "workload_month", nullable = false)
    private String month;

    @Column(nullable = false)
    private int totalMinutes;
}