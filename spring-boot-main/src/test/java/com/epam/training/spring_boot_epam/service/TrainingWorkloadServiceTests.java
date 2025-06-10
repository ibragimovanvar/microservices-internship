package com.epam.training.spring_boot_epam.service;


import com.epam.training.spring_boot_epam.client.TrainingWorkloadClient;
import com.epam.training.spring_boot_epam.domain.Token;
import com.epam.training.spring_boot_epam.domain.User;
import com.epam.training.spring_boot_epam.dto.TrainerWorkloadDTO;
import com.epam.training.spring_boot_epam.dto.TrainerWorkloadResponseDTO;
import com.epam.training.spring_boot_epam.dto.response.ApiResponse;
import com.epam.training.spring_boot_epam.exception.AuthorizationException;
import com.epam.training.spring_boot_epam.exception.DomainException;
import com.epam.training.spring_boot_epam.exception.ForbiddenException;
import com.epam.training.spring_boot_epam.repository.TokenDao;
import com.epam.training.spring_boot_epam.service.impl.TrainingWorkloadServiceImpl;
import com.epam.training.spring_boot_epam.util.DomainUtils;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingWorkloadServiceTests {
    @Mock
    private TrainingWorkloadClient client;
    @Mock
    private TokenDao tokenDao;
    @Mock
    private DomainUtils domainUtils;

    @InjectMocks
    private TrainingWorkloadServiceImpl trainingWorkloadService;

    private Token token;
    private TrainerWorkloadDTO trainerWorkloadDTO;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User("Jane", "Smith", true);
        user.setUsername("anvar_ibragimov");
        token = new Token(1L, "test-token", user.getUsername(), false);
        trainerWorkloadDTO = new TrainerWorkloadDTO();
        trainerWorkloadDTO.setUsername("anvar_ibragimov");
        trainerWorkloadDTO.setFirstName("Jane");
        trainerWorkloadDTO.setLastName("Smith");
        trainerWorkloadDTO.setActive(true);
        trainerWorkloadDTO.setMinutes(60);
        trainerWorkloadDTO.setActionType("ADD");
        trainerWorkloadDTO.setMonth("OCTOBER");
        trainerWorkloadDTO.setYear(2023);
    }

    @Test
    void acceptTrainerWorkload_WhenAuthorizedAndSuccessful_ShouldReturnSuccessResponse() {
        // Arrange
        when(domainUtils.getCurrentUser()).thenReturn(user);
        when(tokenDao.findByUsernameAndExpiredFalse("anvar_ibragimov")).thenReturn(Optional.of(token));
        ApiResponse<Void> apiResponse = new ApiResponse<>(true, "Workload updated", null);
        when(client.acceptTrainerWorkload("Bearer test-token", trainerWorkloadDTO)).thenReturn(apiResponse);

        // Act
        ResponseEntity<ApiResponse<Void>> response = trainingWorkloadService.acceptTrainerWorkload(trainerWorkloadDTO);

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getMessage()).isEqualTo("Workload updated");
        verify(client).acceptTrainerWorkload("Bearer test-token", trainerWorkloadDTO);
    }

    @Test
    void acceptTrainerWorkload_WhenTokenNotFound_ShouldThrowAuthorizationException() {
        // Arrange
        when(domainUtils.getCurrentUser()).thenReturn(user);
        when(tokenDao.findByUsernameAndExpiredFalse("anvar_ibragimov")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> trainingWorkloadService.acceptTrainerWorkload(trainerWorkloadDTO))
                .isInstanceOf(AuthorizationException.class)
                .hasMessage("Please login first");
        verify(client, never()).acceptTrainerWorkload(any(), any());
    }

    @Test
    void getTrainerWorkloadByMonth_WhenAuthorizedAndSuccessful_ShouldReturnWorkloadResponse() {
        // Arrange
        String username = "anvar_ibragimov";
        when(domainUtils.getCurrentUser()).thenReturn(user);
        when(tokenDao.findByUsernameAndExpiredFalse("anvar_ibragimov")).thenReturn(Optional.of(token));
        TrainerWorkloadResponseDTO workloadResponse = new TrainerWorkloadResponseDTO();
        workloadResponse.setUsername(username);
        ApiResponse<TrainerWorkloadResponseDTO> apiResponse = new ApiResponse<>(true, "Success", workloadResponse);
        when(client.getTrainerWorkloadByMonth("Bearer test-token", username)).thenReturn(apiResponse);

        // Act
        ResponseEntity<ApiResponse<TrainerWorkloadResponseDTO>> response = trainingWorkloadService.getTrainerWorkloadByMonth(username);

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getData()).isEqualTo(workloadResponse);
        verify(client).getTrainerWorkloadByMonth("Bearer test-token", username);
    }

    @Test
    void getTrainerWorkloadByMonth_WhenTokenNotFound_ShouldThrowAuthorizationException() {
        // Arrange
        String username = "anvar_ibragimov";
        when(domainUtils.getCurrentUser()).thenReturn(user);
        when(tokenDao.findByUsernameAndExpiredFalse("anvar_ibragimov")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> trainingWorkloadService.getTrainerWorkloadByMonth(username))
                .isInstanceOf(AuthorizationException.class)
                .hasMessage("Please login first");
        verify(client, never()).getTrainerWorkloadByMonth(any(), any());
    }

    @Test
    void extractErrorMessage_WhenValidJsonError_ShouldReturnMessage() {
        // Arrange
        String feignErrorMessage = "400 Bad Request: [{\"message\": \"Invalid input\"}]";

        // Act
        String result = trainingWorkloadService.extractErrorMessage(feignErrorMessage);

        // Assert
        assertThat(result).isEqualTo("Invalid input");
    }

    @Test
    void extractErrorMessage_WhenInvalidJsonFormat_ShouldReturnDefaultMessage() {
        // Arrange
        String feignErrorMessage = "400 Bad Request: invalid json";

        // Act
        String result = trainingWorkloadService.extractErrorMessage(feignErrorMessage);

        // Assert
        assertThat(result).isEqualTo("Unknown error format");
    }

    @Test
    void extractErrorMessage_WhenJsonParsingFails_ShouldReturnDefaultMessage() {
        // Arrange
        String feignErrorMessage = "400 Bad Request: [{malformed json}]";

        // Act
        String result = trainingWorkloadService.extractErrorMessage(feignErrorMessage);

        // Assert
        assertThat(result).isEqualTo("Failed to extract error message");
    }

    @Test
    void fallbackAccept_WhenBadRequestException_ShouldThrowDomainException() {
        // Arrange
        FeignException.BadRequest badRequest = mock(FeignException.BadRequest.class);
        when(badRequest.getMessage()).thenReturn("400 Bad Request: [{\"message\": \"Invalid request data\"}]");

        // Act & Assert
        assertThatThrownBy(() -> trainingWorkloadService.fallbackAccept(trainerWorkloadDTO, badRequest))
                .isInstanceOf(DomainException.class)
                .hasMessage("Invalid request data");
    }

    @Test
    void fallbackAccept_WhenForbiddenException_ShouldThrowForbiddenException() {
        // Arrange
        FeignException.Forbidden forbidden = mock(FeignException.Forbidden.class);
        when(forbidden.getMessage()).thenReturn("403 Forbidden: [{\"message\": \"Access denied\"}]");

        // Act & Assert
        assertThatThrownBy(() -> trainingWorkloadService.fallbackAccept(trainerWorkloadDTO, forbidden))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("Access denied");

    }

    @Test
    void fallbackAccept_WhenGeneralException_ShouldReturnServiceUnavailableResponse() {
        // Arrange
        RuntimeException generalException = new RuntimeException("Service down");

        // Act
        ResponseEntity<ApiResponse<Void>> response = trainingWorkloadService.fallbackAccept(trainerWorkloadDTO, generalException);

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(503);
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).isEqualTo("Training workload service is unavailable");
        assertThat(response.getBody().getData()).isNull();
    }

    @Test
    void fallbackGetByMonth_WhenBadRequestException_ShouldThrowDomainException() {
        // Arrange
        String username = "anvar_ibragimov";
        FeignException.BadRequest badRequest = mock(FeignException.BadRequest.class);
        when(badRequest.getMessage()).thenReturn("400 Bad Request: [{\"message\": \"Invalid username\"}]");

        // Act & Assert
        assertThatThrownBy(() -> trainingWorkloadService.fallbackGetByMonth(username, badRequest))
                .isInstanceOf(DomainException.class)
                .hasMessage("Invalid username");

    }

    @Test
    void fallbackGetByMonth_WhenForbiddenException_ShouldThrowForbiddenException() {
        // Arrange
        String username = "anvar_ibragimov";
        FeignException.Forbidden forbidden = mock(FeignException.Forbidden.class);
        when(forbidden.getMessage()).thenReturn("403 Forbidden: [{\"message\": \"Access denied\"}]");

        // Act & Assert
        assertThatThrownBy(() -> trainingWorkloadService.fallbackGetByMonth(username, forbidden))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("Access denied");

    }

    @Test
    void fallbackGetByMonth_WhenGeneralException_ShouldReturnServiceUnavailableResponse() {
        // Arrange
        String username = "anvar_ibragimov";
        RuntimeException generalException = new RuntimeException("Service down");

        // Act
        ResponseEntity<ApiResponse<TrainerWorkloadResponseDTO>> response = trainingWorkloadService.fallbackGetByMonth(username, generalException);

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(503);
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).isEqualTo("Training workload service is unavailable");
        assertThat(response.getBody().getData()).isNull();
    }
}