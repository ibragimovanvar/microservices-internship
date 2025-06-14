package com.epam.training.spring_boot_epam.event.producer;

import com.epam.training.spring_boot_epam.dto.TrainerWorkloadDTO;
import com.epam.training.spring_boot_epam.event.QueueNames;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrainerWorkloadEventProducer {

    private final JmsTemplate jmsQueueTemplate;

    public void sendToQueue(TrainerWorkloadDTO dto) {
        jmsQueueTemplate.convertAndSend(QueueNames.TRAINER_WORKLOAD_QUEUE, dto, message -> {
            message.setStringProperty("_type", "com.epam.training.dto.TrainerWorkloadDTO");
            return message;
        });
    }


}
