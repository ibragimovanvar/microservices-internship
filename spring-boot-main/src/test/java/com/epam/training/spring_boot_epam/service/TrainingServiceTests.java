package com.epam.training.spring_boot_epam.service;


import com.epam.training.spring_boot_epam.domain.Trainee;
import com.epam.training.spring_boot_epam.domain.Trainer;
import com.epam.training.spring_boot_epam.domain.Training;
import com.epam.training.spring_boot_epam.domain.TrainingType;
import com.epam.training.spring_boot_epam.domain.User;
import com.epam.training.spring_boot_epam.domain.enumeration.DomainStatus;
import com.epam.training.spring_boot_epam.dto.TrainerWorkloadDTO;
import com.epam.training.spring_boot_epam.dto.TrainerWorkloadResponseDTO;
import com.epam.training.spring_boot_epam.dto.TrainingDTO;
import com.epam.training.spring_boot_epam.dto.filters.TraineeTrainingsFilter;
import com.epam.training.spring_boot_epam.dto.filters.TrainerTrainingsFilter;
import com.epam.training.spring_boot_epam.dto.response.ApiResponse;
import com.epam.training.spring_boot_epam.dto.response.TraineeFilterResponseDTO;
import com.epam.training.spring_boot_epam.dto.response.TrainerFilterResponseDTO;
import com.epam.training.spring_boot_epam.exception.DomainException;
import com.epam.training.spring_boot_epam.exception.ServiceUnavailableException;
import com.epam.training.spring_boot_epam.mapper.TrainingMapper;
import com.epam.training.spring_boot_epam.repository.TraineeDao;
import com.epam.training.spring_boot_epam.repository.TrainerDao;
import com.epam.training.spring_boot_epam.repository.TrainingDao;
import com.epam.training.spring_boot_epam.repository.UserDao;
import com.epam.training.spring_boot_epam.service.impl.TrainingServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.epam.training.spring_boot_epam.util.DomainUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTests {

    @Mock
    private TrainingDao trainingDao;
    @Mock
    private TraineeDao traineeDao;
    @Mock
    private TrainerDao trainerDao;
    @Mock
    private UserDao userDao;
    @Mock
    private TrainingMapper trainingMapper;
    @Mock
    private TraineeService traineeService;
    @Mock
    private TrainerService trainerService;
    @Mock
    private TrainingWorkloadService trainingWorkloadService;
    @Mock
    private DomainUtils domainUtils;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    private Trainee trainee;
    private Trainer trainer;
    private Training training;
    private TrainingDTO trainingDTO;

    @BeforeEach
    void setUp() {
        User traineeUser = new User("anvar", "ibragimov", true);
        traineeUser.setUsername("Anvar_ibragimov");
        traineeUser.setPassword("secret");
        trainee = new Trainee(traineeUser, null, null);
        trainee.setId(1L);
        trainee.setTrainings(new ArrayList<>());

        User trainerUser = new User("Jane", "Smith", true);
        trainerUser.setUsername("anvar_ibragimov");
        trainerUser.setPassword("secret");
        TrainingType trainingType = new TrainingType(1L, "Yoga");
        trainer = new Trainer(trainerUser, trainingType);
        trainer.setId(2L);
        trainer.setTrainings(new ArrayList<>());

        training = new Training();
        training.setId(1L);
        training.setTrainingName("Morning Yoga");
        training.setTrainingDateTime(LocalDateTime.of(2023, 10, 1, 9, 0));
        training.setTrainingDurationInMinutes(1);
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingType(trainingType);

        trainingDTO = new TrainingDTO();
        trainingDTO.setTrainingName("Morning Yoga");
        trainingDTO.setTrainingDateTime(LocalDateTime.of(2023, 10, 1, 9, 0));
        trainingDTO.setTrainingDurationInMinutes(60);
        trainingDTO.setTraineeUsername("Anvar_ibragimov");
        trainingDTO.setTrainerUsername("anvar_ibragimov");
    }

    @Test
    void getTraineeTrainings_WhenTrainingsExist_ShouldReturnTraineeFilterResponseDTOs() {
        TraineeTrainingsFilter filter = new TraineeTrainingsFilter("Anvar_ibragimov", null, null, "Jane", "Yoga");
//        when(userDao.findByUsername("Anvar_ibragimov")).thenReturn(Optional.of(trainee.getUser()));
        when(traineeDao.findTraineeTrainings("Anvar_ibragimov", "Anvar_ibragimov", null, null, "Jane", "Yoga")).thenReturn(List.of(training));
        when(domainUtils.getCurrentUser()).thenReturn(trainee.getUser());

        ApiResponse<List<TraineeFilterResponseDTO>> response =
                trainingService.getTraineeTrainings(filter);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).hasSize(1);
        TraineeFilterResponseDTO dto = response.getData().get(0);
        assertThat(dto.getTrainingName()).isEqualTo("Morning Yoga");
        verify(traineeDao).findTraineeTrainings("Anvar_ibragimov", "Anvar_ibragimov",null, null, "Jane", "Yoga");
    }

    @Test
    void getTraineeTrainings_WhenNoTrainingsExist_ShouldReturnEmptyList() {
        TraineeTrainingsFilter filter = new TraineeTrainingsFilter("Anvar_ibragimov", null, null, null, null);
//        when(userDao.findByUsername("Anvar_ibragimov")).thenReturn(Optional.of(trainee.getUser()));
        when(traineeDao.findTraineeTrainings("Anvar_ibragimov", "Anvar_ibragimov",null, null, null, null)).thenReturn(Collections.emptyList());
        when(domainUtils.getCurrentUser()).thenReturn(trainee.getUser());

        ApiResponse<List<TraineeFilterResponseDTO>> response =
                trainingService.getTraineeTrainings(filter);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).isEmpty();
        verify(traineeDao).findTraineeTrainings("Anvar_ibragimov", "Anvar_ibragimov",null, null, null, null);
    }

    @Test
    void getTrainerTrainings_WhenTrainingsExist_ShouldReturnTrainerFilterResponseDTOs() {
        TrainerTrainingsFilter filter = new TrainerTrainingsFilter("anvar_ibragimov", null, null, "anvar");
        when(domainUtils.getCurrentUser()).thenReturn(trainer.getUser());

//        when(userDao.findByUsername("anvar_ibragimov")).thenReturn(Optional.of(trainer.getUser()));
        when(trainerDao.findTrainerTrainings("anvar_ibragimov", "anvar_ibragimov",null, null, "anvar")).thenReturn(List.of(training));

        ApiResponse<List<TrainerFilterResponseDTO>> response =
                trainingService.getTrainerTrainings(filter);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).hasSize(1);
        TrainerFilterResponseDTO dto = response.getData().get(0);
        assertThat(dto.getTraineeFirstname()).isEqualTo("anvar");
        verify(trainerDao).findTrainerTrainings("anvar_ibragimov", "anvar_ibragimov",null, null, "anvar");
    }

    @Test
    void getTrainerTrainings_WhenNoTrainingsExist_ShouldReturnEmptyList() {
        TrainerTrainingsFilter filter = new TrainerTrainingsFilter("anvar_ibragimov", null, null, null);
//        when(userDao.findByUsername("anvar_ibragimov")).thenReturn(Optional.of(trainer.getUser()));
        when(trainerDao.findTrainerTrainings("anvar_ibragimov", "anvar_ibragimov",null, null, null)).thenReturn(Collections.emptyList());
        when(domainUtils.getCurrentUser()).thenReturn(trainer.getUser());

        ApiResponse<List<TrainerFilterResponseDTO>> response =
                trainingService.getTrainerTrainings(filter);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).isEmpty();
        verify(trainerDao).findTrainerTrainings("anvar_ibragimov", "anvar_ibragimov",null, null, null);
    }

    @Test
    void cancelTraining_WhenTrainingNotFound_ShouldThrowDomainException() {
        // When
        Long trainingId = 1L;
        when(trainingDao.findById(trainingId)).thenReturn(Optional.empty());

        // Then
        assertThatThrownBy(() -> trainingService.cancelTraining(trainingId))
                .isInstanceOf(DomainException.class)
                .hasMessage("Training not found");
        verify(trainingDao, never()).delete(ArgumentMatchers.any());
        verify(trainingWorkloadService, never()).acceptTrainerWorkload(any());
    }

    @Test
    void cancelTraining_WhenTrainingDeleted_ShouldThrowDomainException() {
        // When
        Long trainingId = 1L;
        training.setStatus(DomainStatus.DELETED);
        when(trainingDao.findById(trainingId)).thenReturn(Optional.of(training));

        // Then
        assertThatThrownBy(() -> trainingService.cancelTraining(trainingId))
                .isInstanceOf(DomainException.class)
                .hasMessage("Training does not exist");
        verify(trainingDao, never()).delete(any());
        verify(trainingWorkloadService, never()).acceptTrainerWorkload(any());
    }

    @Test
    void getTrainerWorkloadByMonth_WhenSuccessful_ThenShouldReturnWorkloadResponse() {
        // When
        String username = "anvar_ibragimov";
        TrainerWorkloadResponseDTO workloadResponse = new TrainerWorkloadResponseDTO();
        workloadResponse.setUsername(username);
        ApiResponse<TrainerWorkloadResponseDTO> apiResponse = new ApiResponse<>(true, "Success", workloadResponse);
        when(trainingWorkloadService.getTrainerWorkloadByMonth(username))
                .thenReturn(ResponseEntity.ok(apiResponse));

        // When
        ApiResponse<TrainerWorkloadResponseDTO> response = trainingService.getTrainerWorkloadByMonth(username);

        // Then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).isEqualTo(workloadResponse);
        verify(trainingWorkloadService).getTrainerWorkloadByMonth(username);
    }

    @Test
    void getTrainerWorkloadByMonth_WhenServiceFails_ShouldThrowServiceUnavailableException() {
        // When
        String username = "anvar_ibragimov";
        ApiResponse<TrainerWorkloadResponseDTO> apiResponse = new ApiResponse<>(false, "Service error", null);
        when(trainingWorkloadService.getTrainerWorkloadByMonth(username))
                .thenReturn(ResponseEntity.ok(apiResponse));

        // Then
        assertThatThrownBy(() -> trainingService.getTrainerWorkloadByMonth(username))
                .isInstanceOf(ServiceUnavailableException.class)
                .hasMessage("Service error");
        verify(trainingWorkloadService).getTrainerWorkloadByMonth(username);
    }

    @Test
    void addTraining_WhenValidInput_ShouldAddTrainingSuccessfully() {
        // Given
        when(domainUtils.getCurrentUser()).thenReturn(trainer.getUser());
        when(userDao.findByUsername("anvar_ibragimov")).thenReturn(Optional.of(trainer.getUser()));
        when(trainingMapper.toEntity(trainingDTO)).thenReturn(training);
        when(traineeService.getByUsername("Anvar_ibragimov")).thenReturn(trainee);
        when(trainerService.getByUsername("anvar_ibragimov")).thenReturn(trainer);
        when(trainingDao.save(training)).thenReturn(training);
        when(trainingWorkloadService.acceptTrainerWorkload(any(TrainerWorkloadDTO.class)))
                .thenReturn(ResponseEntity.ok(new ApiResponse<>(true, "Workload updated", null)));

        // When
        ApiResponse<Void> response = trainingService.addTraining(trainingDTO);

        // Then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("Successfully created");
        verify(trainingDao).save(training);
        verify(traineeDao).update(trainee);
        verify(trainerDao).update(trainer);
        verify(trainingWorkloadService).acceptTrainerWorkload(any(TrainerWorkloadDTO.class));
        assertThat(trainee.getTrainings()).contains(training);
        assertThat(trainee.getTrainers()).contains(trainer);
        assertThat(trainer.getTrainings()).contains(training);
        assertThat(trainer.getTrainees()).contains(trainee);
    }

    @Test
    void addTraining_WhenWorkloadServiceFails_ShouldThrowServiceUnavailableException() {
        // Given
        when(domainUtils.getCurrentUser()).thenReturn(trainer.getUser());
        when(userDao.findByUsername("anvar_ibragimov")).thenReturn(Optional.of(trainer.getUser()));
        when(trainingMapper.toEntity(trainingDTO)).thenReturn(training);
        when(traineeService.getByUsername("Anvar_ibragimov")).thenReturn(trainee);
        when(trainerService.getByUsername("anvar_ibragimov")).thenReturn(trainer);
        when(trainingDao.save(training)).thenReturn(training);
        when(trainingWorkloadService.acceptTrainerWorkload(any(TrainerWorkloadDTO.class)))
                .thenReturn(ResponseEntity.ok(new ApiResponse<>(false, "Service error", null)));

        // When & Then
        assertThatThrownBy(() -> trainingService.addTraining(trainingDTO))
                .isInstanceOf(ServiceUnavailableException.class)
                .hasMessage("Service error");
        verify(trainingDao).save(training);
        verify(traineeDao).update(trainee);
        verify(trainerDao).update(trainer);
        verify(trainingWorkloadService).acceptTrainerWorkload(any(TrainerWorkloadDTO.class));
    }

    @Test
    void addTraining_WhenTraineeAlreadyHasTrainer_ShouldNotDuplicateTrainer() {
        // Given
        trainee.getTrainers().add(trainer);
        when(domainUtils.getCurrentUser()).thenReturn(trainer.getUser());
        when(userDao.findByUsername("anvar_ibragimov")).thenReturn(Optional.of(trainer.getUser()));
        when(trainingMapper.toEntity(trainingDTO)).thenReturn(training);
        when(traineeService.getByUsername("Anvar_ibragimov")).thenReturn(trainee);
        when(trainerService.getByUsername("anvar_ibragimov")).thenReturn(trainer);
        when(trainingDao.save(training)).thenReturn(training);
        when(trainingWorkloadService.acceptTrainerWorkload(any(TrainerWorkloadDTO.class)))
                .thenReturn(ResponseEntity.ok(new ApiResponse<>(true, "Workload updated", null)));

        // When
        ApiResponse<Void> response = trainingService.addTraining(trainingDTO);

        // Then
        assertThat(response.isSuccess()).isTrue();
        assertThat(trainee.getTrainers()).hasSize(1); // No duplicate trainer
        assertThat(trainee.getTrainings()).contains(training);
        verify(traineeDao).update(trainee);
        verify(trainerDao).update(trainer);
        verify(trainingWorkloadService).acceptTrainerWorkload(any(TrainerWorkloadDTO.class));
    }

    @Test
    void addTraining_WhenTrainerAlreadyHasTrainee_ShouldNotDuplicateTrainee() {
        // Given
        trainer.getTrainees().add(trainee);
        when(domainUtils.getCurrentUser()).thenReturn(trainer.getUser());
        when(userDao.findByUsername("anvar_ibragimov")).thenReturn(Optional.of(trainer.getUser()));
        when(trainingMapper.toEntity(trainingDTO)).thenReturn(training);
        when(traineeService.getByUsername("Anvar_ibragimov")).thenReturn(trainee);
        when(trainerService.getByUsername("anvar_ibragimov")).thenReturn(trainer);
        when(trainingDao.save(training)).thenReturn(training);
        when(trainingWorkloadService.acceptTrainerWorkload(any(TrainerWorkloadDTO.class)))
                .thenReturn(ResponseEntity.ok(new ApiResponse<>(true, "Workload updated", null)));

        // When
        ApiResponse<Void> response = trainingService.addTraining(trainingDTO);

        // Then
        assertThat(response.isSuccess()).isTrue();
        assertThat(trainer.getTrainees()).hasSize(1); // No duplicate trainee
        assertThat(trainer.getTrainings()).contains(training);
        verify(traineeDao).update(trainee);
        verify(trainerDao).update(trainer);
        verify(trainingWorkloadService).acceptTrainerWorkload(any(TrainerWorkloadDTO.class));
    }

    @Test
    void addTraining_WhenTrainingAlreadyExistsForTrainee_ShouldNotAddDuplicateTraining() {
        // Given
        trainee.getTrainings().add(training);
        when(domainUtils.getCurrentUser()).thenReturn(trainer.getUser());
        when(userDao.findByUsername("anvar_ibragimov")).thenReturn(Optional.of(trainer.getUser()));
        when(trainingMapper.toEntity(trainingDTO)).thenReturn(training);
        when(traineeService.getByUsername("Anvar_ibragimov")).thenReturn(trainee);
        when(trainerService.getByUsername("anvar_ibragimov")).thenReturn(trainer);
        when(trainingDao.save(training)).thenReturn(training);
        when(trainingWorkloadService.acceptTrainerWorkload(any(TrainerWorkloadDTO.class)))
                .thenReturn(ResponseEntity.ok(new ApiResponse<>(true, "Workload updated", null)));

        // When
        ApiResponse<Void> response = trainingService.addTraining(trainingDTO);

        // Then
        assertThat(response.isSuccess()).isTrue();
        assertThat(trainee.getTrainings()).hasSize(1); // No duplicate training
        verify(traineeDao, never()).update(trainee); // No update if training already exists
        verify(trainerDao).update(trainer);
        verify(trainingWorkloadService).acceptTrainerWorkload(any(TrainerWorkloadDTO.class));
    }

    @Test
    void addTraining_WhenTrainingAlreadyExistsForTrainer_ShouldNotAddDuplicateTraining() {
        // Given
        trainer.getTrainings().add(training);
        when(domainUtils.getCurrentUser()).thenReturn(trainer.getUser());
        when(userDao.findByUsername("anvar_ibragimov")).thenReturn(Optional.of(trainer.getUser()));
        when(trainingMapper.toEntity(trainingDTO)).thenReturn(training);
        when(traineeService.getByUsername("Anvar_ibragimov")).thenReturn(trainee);
        when(trainerService.getByUsername("anvar_ibragimov")).thenReturn(trainer);
        when(trainingDao.save(training)).thenReturn(training);
        when(trainingWorkloadService.acceptTrainerWorkload(any(TrainerWorkloadDTO.class)))
                .thenReturn(ResponseEntity.ok(new ApiResponse<>(true, "Workload updated", null)));

        // When
        ApiResponse<Void> response = trainingService.addTraining(trainingDTO);

        // Then
        assertThat(response.isSuccess()).isTrue();
        assertThat(trainer.getTrainings()).hasSize(1); // No duplicate training
        verify(trainerDao, never()).update(trainer); // No update if training already exists
        verify(traineeDao).update(trainee);
        verify(trainingWorkloadService).acceptTrainerWorkload(any(TrainerWorkloadDTO.class));
    }
}