package com.epam.training.repository;

import com.epam.training.domain.TrainerWorkload;
import com.epam.training.domain.enums.Month;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest()
public class TrainerWorkloadRepositoryTests {

    @Autowired
    private TrainerWorkloadRepository trainerWorkloadRepository;

    private TrainerWorkload tw;


    @BeforeEach
    public void setUp() {
        tw = new TrainerWorkload();
        tw.setFirstName("Anvar");
        tw.setLastName("Ibragimov");
        tw.setUsername("anvar_ibragimov");
        tw.setYear(2025);
        tw.setMonth(Month.AUGUST.name());
        tw.setTotalMinutes(320);
        tw.setActive(true);
    }


    /**
     * Test for save method
     */
    @Test
    public void givenTrainerWorkloadObject_whenSave_thenReturnSaved() {
        // when - action
        TrainerWorkload saved = trainerWorkloadRepository.save(tw);

        // then - result
        assertThat(saved).isNotNull();
        assertThat(saved.getFirstName()).isEqualTo("Anvar");
        assertThat(saved.getLastName()).isEqualTo("Ibragimov");
        assertThat(saved.getUsername()).isEqualTo("anvar_ibragimov");
        assertThat(saved.getYear()).isEqualTo(2025);
        assertThat(saved.getMonth()).isEqualTo(Month.AUGUST.name());
        assertThat(saved.getTotalMinutes()).isEqualTo(320);
        assertThat(saved.isActive()).isTrue();
    }

    /**
     * Test for find by username and year and month
     */
    @Test
    public void givenTrainerUsernameYearMonth_whenFindByUsernameAndYearAndMonth_thenReturnTrainerWorkload() {
        // given - setup
        trainerWorkloadRepository.save(tw);

        // when - action
        Optional<TrainerWorkload> foundTrainerWorkload = trainerWorkloadRepository.findByUsernameAndYearAndMonth(tw.getUsername(), tw.getYear(), tw.getMonth());
        assertThat(foundTrainerWorkload.isPresent()).isTrue();

        // then - result
        TrainerWorkload trainerWorkload = foundTrainerWorkload.get();
        assertThat(trainerWorkload.getId()).isNotNull();
        assertThat(trainerWorkload.getFirstName()).isEqualTo("Anvar");
        assertThat(trainerWorkload.getLastName()).isEqualTo("Ibragimov");
        assertThat(trainerWorkload.getUsername()).isEqualTo("anvar_ibragimov");
        assertThat(trainerWorkload.getYear()).isEqualTo(2025);
        assertThat(trainerWorkload.getMonth()).isEqualTo(Month.AUGUST.name());
        assertThat(trainerWorkload.getTotalMinutes()).isEqualTo(320);
        assertThat(trainerWorkload.isActive()).isTrue();
    }


    /**
     * Test to verify that findByUsername retrieves all workload records for a given username.
     */
    @Test
    public void givenUsername_whenFindByUsername_thenReturnListOfTrainerWorkloads() {
        TrainerWorkload tw2 = new TrainerWorkload();
        tw2.setFirstName("Anvar");
        tw2.setLastName("Ibragimov");
        tw2.setUsername("anvar_ibragimov");
        tw2.setYear(2024);
        tw2.setMonth(Month.JULY.name());
        tw2.setTotalMinutes(150);
        tw2.setActive(true);

        trainerWorkloadRepository.save(tw);   // 2025 AUGUST
        trainerWorkloadRepository.save(tw2);  // 2024 JULY

        // when - action
        Optional<List<TrainerWorkload>> result = trainerWorkloadRepository.findByUsername("anvar_ibragimov");

        // then - assert
        assertThat(result.isPresent()).isTrue();
        List<TrainerWorkload> workloads = result.get();
        assertThat(workloads).hasSize(2);
        assertThat(workloads).extracting(TrainerWorkload::getYear).containsExactlyInAnyOrder(2025, 2024);
    }

    /**
     * Test to verify that existsTrainerByUsername returns true when username exists,
     * and false when it doesn't.
     */
    @Test
    public void givenUsername_whenExistsTrainerByUsername_thenReturnTrueOrFalse() {
        trainerWorkloadRepository.save(tw);

        boolean exists = trainerWorkloadRepository.existsTrainerByUsername("anvar_ibragimov");

        assertThat(exists).isTrue();

        boolean notExists = trainerWorkloadRepository.existsTrainerByUsername("unknown_user");

        assertThat(notExists).isFalse();
    }

}
