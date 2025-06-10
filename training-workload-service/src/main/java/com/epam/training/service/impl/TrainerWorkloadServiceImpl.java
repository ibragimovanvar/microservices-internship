package com.epam.training.service.impl;

import com.epam.training.domain.TrainerWorkload;
import com.epam.training.dto.MonthlyDataDTO;
import com.epam.training.dto.TrainerWorkloadDTO;
import com.epam.training.dto.TrainerWorkloadResponseDTO;
import com.epam.training.dto.response.ApiResponse;
import com.epam.training.exception.DomainException;
import com.epam.training.repository.TrainerWorkloadRepository;
import com.epam.training.service.TrainerWorkloadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainerWorkloadServiceImpl implements TrainerWorkloadService {

    private final TrainerWorkloadRepository trainerWorkloadRepository;

    @Override
    public ApiResponse<Void> acceptTrainerWorkload(TrainerWorkloadDTO dto) {
        Optional<TrainerWorkload> trainerWorkload = trainerWorkloadRepository.findByUsernameAndYearAndMonth(dto.getUsername(), dto.getYear(), dto.getMonth());

        if (trainerWorkload.isEmpty()) {
            if (dto.getActionType().equals("DELETE")) {
                throw new DomainException("You are not allowed to delete a trainer workload that doesn't exist");
            }

            TrainerWorkload newTrainerWorkload = new TrainerWorkload();
            newTrainerWorkload.setFirstName(dto.getFirstName());
            newTrainerWorkload.setLastName(dto.getLastName());
            newTrainerWorkload.setUsername(dto.getUsername());
            newTrainerWorkload.setYear(dto.getYear());
            newTrainerWorkload.setMonth(dto.getMonth());
            newTrainerWorkload.setTotalMinutes(dto.getMinutes());
            newTrainerWorkload.setActive(dto.isActive());
            trainerWorkloadRepository.save(newTrainerWorkload);
            return new ApiResponse<>(true, "Trainer workload created successfully !", null);
        }

        TrainerWorkload existingTrainerWorkload = trainerWorkload.get();
        existingTrainerWorkload.setFirstName(dto.getFirstName());
        existingTrainerWorkload.setLastName(dto.getLastName());
        existingTrainerWorkload.setUsername(dto.getUsername());
        existingTrainerWorkload.setYear(dto.getYear());
        existingTrainerWorkload.setMonth(dto.getMonth());
        existingTrainerWorkload.setActive(dto.isActive());

        if (dto.getActionType().equals("ADD")) {
            existingTrainerWorkload.setTotalMinutes(existingTrainerWorkload.getTotalMinutes() + dto.getMinutes());
        } else {
            if (existingTrainerWorkload.getTotalMinutes() < dto.getMinutes()) {
                throw new DomainException("Error while removing working hours");
            }
            existingTrainerWorkload.setTotalMinutes(existingTrainerWorkload.getTotalMinutes() - dto.getMinutes());
        }
        trainerWorkloadRepository.save(existingTrainerWorkload);

        return new ApiResponse<>(true, "Trainer workload created successfully !", null);
    }

    @Override
    public ApiResponse<TrainerWorkloadResponseDTO> getTrainersWorkload(String username) {
        Optional<List<TrainerWorkload>> optionalTrainerWorkloads = trainerWorkloadRepository.findByUsername(username);

        if (!trainerWorkloadRepository.existsTrainerByUsername(username)) {
            throw new DomainException("There are no data available with a username of " + username);
        }

        List<TrainerWorkload> trainerWorkloads = optionalTrainerWorkloads.get();

        if(!trainerWorkloads.isEmpty()) {
            TrainerWorkload firstRecord = trainerWorkloads.get(0);

            Map<Integer, Map<String, TrainerWorkload>> groupedData = trainerWorkloads.stream()
                    .collect(Collectors.groupingBy(
                            TrainerWorkload::getYear,
                            Collectors.toMap(
                                    TrainerWorkload::getMonth,  // now String
                                    tw -> tw
                            )
                    ));

            Map<String, List<MonthlyDataDTO>> monthlyDataMap = new TreeMap<>(); // sorted by year

            String[] months = {
                    "JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE",
                    "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"
            };

            for (Integer year : groupedData.keySet()) {
                Map<String, TrainerWorkload> monthMap = groupedData.get(year);
                List<MonthlyDataDTO> monthList = new ArrayList<>();

                for (String month : months) {
                    TrainerWorkload tw = monthMap.get(month);
                    int minutes = (tw != null) ? tw.getTotalMinutes() : 0;
                    BigDecimal hours = BigDecimal.valueOf(minutes).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);

                    MonthlyDataDTO dto = new MonthlyDataDTO(month, minutes, hours);
                    monthList.add(dto);
                }

                monthlyDataMap.put(String.valueOf(year), monthList);
            }

            TrainerWorkloadResponseDTO responseDTO = new TrainerWorkloadResponseDTO(
                    firstRecord.getUsername(),
                    firstRecord.getFirstName(),
                    firstRecord.getLastName(),
                    firstRecord.isActive(),
                    monthlyDataMap
            );
            return new ApiResponse<>(true, "Successfully retreived data of Monthly data of trainer workload", responseDTO);
        }

        return new ApiResponse<>(true, "Monthly data is empty for given trainer", null);
    }
}
