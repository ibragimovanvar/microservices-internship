package com.epam.training.event.listener;

import com.epam.training.dto.TrainerWorkloadDTO;
import com.epam.training.event.QueueNames;
import com.epam.training.service.TrainerWorkloadService;
import com.epam.training.util.DomainUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrainerWorkloadEventListener {
    private final TrainerWorkloadService trainerWorkloadService;

    @JmsListener(destination = QueueNames.TRAINER_WORKLOAD_QUEUE)
    public void listenQueue(TrainerWorkloadDTO dto) {
        log.info("📥 Received from Queue: {}", dto.getUsername());

        trainerWorkloadService.acceptTrainerWorkload(dto);
    }

}
