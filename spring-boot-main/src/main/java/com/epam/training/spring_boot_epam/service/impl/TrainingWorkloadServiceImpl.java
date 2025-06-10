package com.epam.training.spring_boot_epam.service.impl;

import com.epam.training.spring_boot_epam.client.TrainingWorkloadClient;
import com.epam.training.spring_boot_epam.domain.Token;
import com.epam.training.spring_boot_epam.dto.TrainerWorkloadDTO;
import com.epam.training.spring_boot_epam.dto.TrainerWorkloadResponseDTO;
import com.epam.training.spring_boot_epam.dto.response.ApiResponse;
import com.epam.training.spring_boot_epam.exception.AuthorizationException;
import com.epam.training.spring_boot_epam.exception.DomainException;
import com.epam.training.spring_boot_epam.exception.ForbiddenException;
import com.epam.training.spring_boot_epam.repository.TokenDao;
import com.epam.training.spring_boot_epam.service.TrainingWorkloadService;
import com.epam.training.spring_boot_epam.util.DomainUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TrainingWorkloadServiceImpl implements TrainingWorkloadService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainingWorkloadServiceImpl.class);

    private final TrainingWorkloadClient client;
    private final TokenDao tokenDao;
    private final DomainUtils domainUtils;

    public TrainingWorkloadServiceImpl(TrainingWorkloadClient client, TokenDao tokenDao, DomainUtils domainUtils) {
        this.client = client;
        this.tokenDao = tokenDao;
        this.domainUtils = domainUtils;
    }

    @CircuitBreaker(name = "TRAINING-WORKLOAD-SERVICE-CB", fallbackMethod = "fallbackAccept")
    public ResponseEntity<ApiResponse<Void>> acceptTrainerWorkload(TrainerWorkloadDTO dto) {
        Optional<Token> token = tokenDao.findByUsernameAndExpiredFalse(domainUtils.getCurrentUser().getUsername());

        if (token.isEmpty()) {
            throw new AuthorizationException("Please login first");
        }

        return ResponseEntity.ok(client.acceptTrainerWorkload("Bearer " + token.get().getToken(), dto));
    }

    public ResponseEntity<ApiResponse<Void>> fallbackAccept(TrainerWorkloadDTO dto, Throwable t) {
        if (t instanceof FeignException.BadRequest feignException) {
            throw new DomainException(extractErrorMessage(feignException.getMessage()));
        } else if (t instanceof FeignException.Unauthorized feignException) {
            throw new AuthorizationException("Please authorize first !");
        }else if (t instanceof FeignException.Forbidden feignException) {
            throw new ForbiddenException(extractErrorMessage(feignException.getMessage()));
        }

        ApiResponse<Void> fallback =
                new ApiResponse<>(false, "Training workload service is unavailable", null);
        LOGGER.error(fallback.getMessage(), t);
        return ResponseEntity.status(503).body(fallback);
    }

    @CircuitBreaker(name = "TRAINING-WORKLOAD-SERVICE-CB", fallbackMethod = "fallbackGetByMonth")
    public ResponseEntity<ApiResponse<TrainerWorkloadResponseDTO>> getTrainerWorkloadByMonth(String username) {
        Optional<Token> token = tokenDao.findByUsernameAndExpiredFalse(domainUtils.getCurrentUser().getUsername());

        if (token.isEmpty()) {
            throw new AuthorizationException("Please login first");
        }

        ApiResponse<TrainerWorkloadResponseDTO> response = client.getTrainerWorkloadByMonth("Bearer " + token.get().getToken(), username);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<ApiResponse<TrainerWorkloadResponseDTO>> fallbackGetByMonth(String username, Throwable t) {
        if (t instanceof FeignException.BadRequest feignException) {
            throw new DomainException(extractErrorMessage(feignException.getMessage()));
        } else if (t instanceof FeignException.Unauthorized feignException) {
            throw new AuthorizationException("Please authorize first !");
        }else if (t instanceof FeignException.Forbidden feignException) {
            throw new ForbiddenException(extractErrorMessage(feignException.getMessage()));
        }

        ApiResponse<TrainerWorkloadResponseDTO> fallback =
                new ApiResponse<>(false, "Training workload service is unavailable", null);
        LOGGER.error(fallback.getMessage(), t);
        return ResponseEntity.status(503).body(fallback);
    }

    public String extractErrorMessage(String feignErrorMessage) {
        try {
            int jsonStart = feignErrorMessage.indexOf("[{");
            int jsonEnd = feignErrorMessage.indexOf("}]") + 2;
            if (jsonStart == -1 || jsonEnd == -1) {
                return "Unknown error format";
            }

            String jsonPart = feignErrorMessage.substring(jsonStart + 1, jsonEnd); // remove outer brackets []
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(jsonPart);
            return jsonNode.get("message").asText();
        } catch (Exception e) {
            return "Failed to extract error message";
        }
    }
}
