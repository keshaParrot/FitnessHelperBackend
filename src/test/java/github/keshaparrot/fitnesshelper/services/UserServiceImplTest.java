package github.keshaparrot.fitnesshelper.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import github.keshaparrot.fitnesshelper.domain.dto.UpdateUserDataRequest;
import github.keshaparrot.fitnesshelper.domain.dto.UserDTO;
import github.keshaparrot.fitnesshelper.domain.entity.UserProfile;
import github.keshaparrot.fitnesshelper.domain.mappers.UserMapper;
import github.keshaparrot.fitnesshelper.repository.UserRepository;
import github.keshaparrot.fitnesshelper.utils.exceptions.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private UserProfile sampleUser;
    private UserDTO sampleDto;

    @BeforeEach
    void setUp() {
        sampleUser = new UserProfile();
        sampleUser.setId(1L);
        sampleUser.setEmail("john.doe@example.com");
        sampleUser.setFirstName("John");
        sampleUser.setLastName("Doe");
        sampleUser.setDateOfBirth(LocalDate.of(1990, 1, 1));
        sampleUser.setGender("MALE");
        sampleUser.setHeightCm(180);
        sampleUser.setWeightKg(75.0);

        sampleDto = new UserDTO();
        sampleDto.setId(1L);
        sampleDto.setEmail("john.doe@example.com");
        sampleDto.setFirstName("John");
        sampleDto.setLastName("Doe");
        sampleDto.setDateOfBirth(LocalDate.of(1990, 1, 1));
        sampleDto.setGender("MALE");
        sampleDto.setHeightCm(180);
        sampleDto.setWeightKg(75.0);
    }

    @Test
    @DisplayName("getById returns DTO when user exists")
    void getByIdSuccess() {
        when(userRepository.getUserProfileById(1L)).thenReturn(Optional.of(sampleUser));
        when(userMapper.toDto(sampleUser)).thenReturn(sampleDto);

        UserDTO result = userService.getById(1L);

        assertNotNull(result);
        assertEquals(sampleDto, result);
        verify(userRepository).getUserProfileById(1L);
        verify(userMapper).toDto(sampleUser);
    }

    @Test
    @DisplayName("getById throws UserNotFoundException when user not found")
    void getByIdNotFound() {
        when(userRepository.getUserProfileById(2L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getById(2L));
        verify(userRepository).getUserProfileById(2L);
        verifyNoMoreInteractions(userMapper);
    }

    @Test
    @DisplayName("getEntityById returns entity when user exists")
    void getEntityByIdSuccess() {
        when(userRepository.getUserProfileById(1L)).thenReturn(Optional.of(sampleUser));

        UserProfile result = userService.getEntityById(1L);

        assertNotNull(result);
        assertEquals(sampleUser, result);
        verify(userRepository).getUserProfileById(1L);
    }

    @Test
    @DisplayName("getEntityById throws UserNotFoundException when user not found")
    void getEntityByIdNotFound() {
        when(userRepository.getUserProfileById(3L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getEntityById(3L));
        verify(userRepository).getUserProfileById(3L);
    }

    @Test
    @DisplayName("update updates only non-null fields and returns DTO")
    void updatePartialFields() {
        UpdateUserDataRequest request = UpdateUserDataRequest.builder()
                .email("john.doe@example.com")
                .firstName("Jonathan")
                .lastName("Smith")
                .dateOfBirth(LocalDate.of(1985, 5, 20))
                .gender("OTHER")
                .heightCm(175)
                .weightKg(70.0)
                .build();

        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(UserProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toDto(any(UserProfile.class))).thenReturn(sampleDto);

        UserDTO result = userService.update("john.doe@example.com", request);

        ArgumentCaptor<UserProfile> captor = ArgumentCaptor.forClass(UserProfile.class);
        verify(userRepository).save(captor.capture());
        UserProfile saved = captor.getValue();

        assertEquals("Jonathan", saved.getFirstName());
        assertEquals("Smith", saved.getLastName());
        assertEquals(LocalDate.of(1985, 5, 20), saved.getDateOfBirth());
        assertEquals("OTHER", saved.getGender());
        assertEquals(175, saved.getHeightCm());
        assertEquals(70, saved.getWeightKg());

        assertEquals(sampleDto, result);
        verify(userMapper).toDto(saved);
    }

    @Test
    @DisplayName("update throws UserNotFoundException when email not found")
    void updateNotFound() {
        UpdateUserDataRequest request = UpdateUserDataRequest.builder()
                .email("nonexistent@example.com")
                .build();

        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.update("nonexistent@example.com", request)
        );
        verify(userRepository).findByEmail("nonexistent@example.com");
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    @DisplayName("update with null fields does not change entity")
    void updateWithNulls() {
        UpdateUserDataRequest request = UpdateUserDataRequest.builder()
                .email("john.doe@example.com")
                .build();

        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(UserProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toDto(sampleUser)).thenReturn(sampleDto);

        UserDTO result = userService.update("john.doe@example.com", request);

        assertEquals("John", sampleUser.getFirstName());
        assertEquals("Doe", sampleUser.getLastName());
        assertEquals(LocalDate.of(1990, 1, 1), sampleUser.getDateOfBirth());
        assertEquals("MALE", sampleUser.getGender());
        assertEquals(180, sampleUser.getHeightCm());
        assertEquals(75, sampleUser.getWeightKg());

        assertEquals(sampleDto, result);
        verify(userRepository).save(sampleUser);
        verify(userMapper).toDto(sampleUser);
    }
}
