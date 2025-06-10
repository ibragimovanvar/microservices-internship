package com.epam.training.service;

import com.epam.training.domain.TrainerWorkload;
import com.epam.training.domain.enums.Month;
import com.epam.training.dto.TrainerWorkloadDTO;
import com.epam.training.dto.TrainerWorkloadResponseDTO;
import com.epam.training.dto.response.ApiResponse;
import com.epam.training.repository.TrainerWorkloadRepository;
import com.epam.training.service.impl.TrainerWorkloadServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class TrainerWorkloadServiceTests {

    @Mock
    private TrainerWorkloadRepository trainerWorkloadRepository;

    @InjectMocks
    private TrainerWorkloadServiceImpl trainerWorkloadService;

    private TrainerWorkloadDTO trainerWorkloadDTO;
    private TrainerWorkload tw;

    @BeforeEach
    public void setUp() {
        // Given DTO
        trainerWorkloadDTO = new TrainerWorkloadDTO();
        trainerWorkloadDTO.setFirstName("Anvar");
        trainerWorkloadDTO.setLastName("Ibragimov");
        trainerWorkloadDTO.setUsername("anvar_ibragimov");
        trainerWorkloadDTO.setYear(2025);
        trainerWorkloadDTO.setMonth(Month.AUGUST.name());
        trainerWorkloadDTO.setMinutes(320);
        trainerWorkloadDTO.setActive(true);
        trainerWorkloadDTO.setActionType("ADD");

        // Saved Object
        tw = new TrainerWorkload();
        tw.setId(1L);
        tw.setFirstName("Anvar");
        tw.setLastName("Ibragimov");
        tw.setUsername("anvar_ibragimov");
        tw.setYear(2025);
        tw.setMonth(Month.AUGUST.name());
        tw.setTotalMinutes(320);
        tw.setActive(true);
    }

    /**
     * Test for Trainer Workload object is working properly or not
     */
    @Test
    public void givenNewTrainerWorkloadObject_whenAcceptTrainerWorkload_thenReturnSaved() {
        BDDMockito.given(trainerWorkloadRepository.findByUsernameAndYearAndMonth(tw.getUsername(), tw.getYear(), tw.getMonth())).willReturn(Optional.empty());
        BDDMockito.given(trainerWorkloadRepository.save(any(TrainerWorkload.class))).willReturn(tw);

        ApiResponse<Void> response = trainerWorkloadService.acceptTrainerWorkload(trainerWorkloadDTO);

        System.out.println("[INFO] RESPONSE: " + response);

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    public void givenTrainerWorkloadObject_whenAcceptTrainerWorkload_thenReturnSaved() {
        BDDMockito.given(trainerWorkloadRepository.findByUsernameAndYearAndMonth(tw.getUsername(), tw.getYear(), tw.getMonth())).willReturn(Optional.of(tw));
        BDDMockito.given(trainerWorkloadRepository.save(any(TrainerWorkload.class))).willReturn(tw);

        ApiResponse<Void> response = trainerWorkloadService.acceptTrainerWorkload(trainerWorkloadDTO);

        System.out.println("[INFO] RESPONSE: " + response);

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    /**
     * Test for getting monthly data of given trainer
     */
    @Test
    public void givenValidUsername_whenGetTrainersWorkload_thenReturnResponseDTO() {
        String username = "anvar_ibragimov";

        List<TrainerWorkload> workloadList = getTrainerWorkloads(username);

        BDDMockito.given(trainerWorkloadRepository.existsTrainerByUsername(username)).willReturn(true);
        BDDMockito.given(trainerWorkloadRepository.findByUsername(username)).willReturn(Optional.of(workloadList));

        ApiResponse<?> response = trainerWorkloadService.getTrainersWorkload(username);

        System.out.println("[INFO] RESPONSE: " + response);

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNotNull(response.getData());

        TrainerWorkloadResponseDTO dto = (TrainerWorkloadResponseDTO) response.getData();

        Assertions.assertEquals(username, dto.getUsername());
        Assertions.assertEquals("Anvar", dto.getFirstName());
        Assertions.assertEquals("Ibragimov", dto.getLastName());
        Assertions.assertTrue(dto.isActive());

        Assertions.assertTrue(dto.getMonthlyData().containsKey("2025"));
        Assertions.assertEquals(12, dto.getMonthlyData().get("2025").size());

        Assertions.assertEquals(120, dto.getMonthlyData().get("2025").get(0).getDurationInMinutes());
        Assertions.assertEquals("JANUARY", dto.getMonthlyData().get("2025").get(0).getMonth());
    }

    private static List<TrainerWorkload> getTrainerWorkloads(String username) {
        TrainerWorkload tw1 = new TrainerWorkload();
        tw1.setUsername(username);
        tw1.setFirstName("Anvar");
        tw1.setLastName("Ibragimov");
        tw1.setActive(true);
        tw1.setYear(2025);
        tw1.setMonth("JANUARY");
        tw1.setTotalMinutes(120);

        TrainerWorkload tw2 = new TrainerWorkload();
        tw2.setUsername(username);
        tw2.setFirstName("Anvar");
        tw2.setLastName("Ibragimov");
        tw2.setActive(true);
        tw2.setYear(2025);
        tw2.setMonth("FEBRUARY");
        tw2.setTotalMinutes(180);

        return List.of(tw1, tw2);
    }

}