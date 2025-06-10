package com.epam.training.controller;

import com.epam.training.domain.enums.Month;
import com.epam.training.dto.MonthlyDataDTO;
import com.epam.training.dto.TrainerWorkloadDTO;
import com.epam.training.dto.TrainerWorkloadResponseDTO;
import com.epam.training.dto.response.ApiResponse;
import com.epam.training.service.impl.TrainerWorkloadServiceImpl;
import com.epam.training.util.DomainUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@AutoConfigureMockMvc(addFilters = false)
public class TrainerWorkloadControllerIntegrationTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TrainerWorkloadServiceImpl trainerWorkloadService;

    @MockBean
    private DomainUtils domainUtils;

    private static final String API_PATH = "/v1/trainers-workload";


    @BeforeEach
    void setUp() {
    }

    /**
     * Test for
     */
    @Test
    public void givenTrainerWorkloadDTO_whenAcceptTrainerWorkload_thenReturnSuccess() throws Exception {
        // given - setup
        TrainerWorkloadDTO trainerWorkloadDTO = new TrainerWorkloadDTO();
        trainerWorkloadDTO.setFirstName("Trainer");
        trainerWorkloadDTO.setLastName("Ibragimov");
        trainerWorkloadDTO.setUsername("trainer_ibragimov");
        trainerWorkloadDTO.setYear(2025);
        trainerWorkloadDTO.setMonth(Month.AUGUST.name());
        trainerWorkloadDTO.setMinutes(320);
        trainerWorkloadDTO.setActive(true);
        trainerWorkloadDTO.setActionType("ADD");

        BDDMockito.given(trainerWorkloadService.acceptTrainerWorkload(trainerWorkloadDTO))
                .willAnswer(invocationOnMock -> new ApiResponse<>(true, "Trainer workload created successfully !", null));
        BDDMockito.given(domainUtils.getCurrentUserUsername()).willReturn(trainerWorkloadDTO.getUsername());
        BDDMockito.willDoNothing().given(domainUtils).checkUsername(trainerWorkloadDTO.getUsername());

        // when - action
        ResultActions response = mvc.perform(post(API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(trainerWorkloadDTO))
        );

        // then - result
        response.andDo(print())
                .andExpect(status().isCreated());
    }


    @Test
    public void givenUsername_whenGetTrainerWorkloadByMonth_thenReturnWorkload() throws Exception {
        // given
        String username = "trainer_ibragimov";

        // Monthly data DTO list
        List<MonthlyDataDTO> monthlyDataList = new ArrayList<>();
        monthlyDataList.add(new MonthlyDataDTO("AUGUST", 320, BigDecimal.valueOf(5.33)));

        Map<String, List<MonthlyDataDTO>> monthlyDataMap = new HashMap<>();
        monthlyDataMap.put("2025", monthlyDataList);

        TrainerWorkloadResponseDTO responseDTO = new TrainerWorkloadResponseDTO(
                username,
                "Trainer",
                "Ibragimov",
                true,
                monthlyDataMap
        );

        ApiResponse<TrainerWorkloadResponseDTO> apiResponse = new ApiResponse<>(
                true,
                "Successfully retreived data of Monthly data of trainer workload",
                responseDTO
        );

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        BDDMockito.given(domainUtils.getCurrentUserUsername()).willReturn(username);
        BDDMockito.willDoNothing().given(domainUtils).checkUsername(username);
        BDDMockito.given(trainerWorkloadService.getTrainersWorkload(username)).willReturn(apiResponse);

        // when
        ResultActions response = mvc.perform(MockMvcRequestBuilders.get("/v1/trainers-workload/by-month")
                        .param("username", username)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Successfully retreived data of Monthly data of trainer workload"))
                .andExpect(jsonPath("$.data.username").value("trainer_ibragimov"))
                .andExpect(jsonPath("$.data.firstName").value("Trainer"))
                .andExpect(jsonPath("$.data.lastName").value("Ibragimov"))
                .andExpect(jsonPath("$.data.active").value(true));
    }

}
