package github.keshaparrot.fitnesshelper.services;

import github.keshaparrot.fitnesshelper.domain.dto.CreateUserRequest;
import github.keshaparrot.fitnesshelper.domain.dto.UpdateUserDataRequest;
import github.keshaparrot.fitnesshelper.domain.dto.UserDTO;
import github.keshaparrot.fitnesshelper.domain.entity.UserProfile;
import github.keshaparrot.fitnesshelper.domain.mappers.UserMapper;
import github.keshaparrot.fitnesshelper.repository.UserRepository;
import github.keshaparrot.fitnesshelper.utils.exceptions.DuplicateEmailException;
import github.keshaparrot.fitnesshelper.utils.exceptions.UserNotFoundException;
import org.hibernate.mapping.Any;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserMapper userMapper;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        lenient().when(userMapper.toDto(any(UserProfile.class))).thenAnswer(invocation -> {
            UserProfile user = invocation.getArgument(0);
            return UserDTO.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .password(user.getPassword())
                    .dateOfBirth(user.getDateOfBirth())
                    .gender(user.getGender())
                    .build();
        });
        userService = new UserServiceImpl(userRepository, passwordEncoder, userMapper);
    }

    @Test
    void register_shouldSaveUser_whenEmailIsUnique() {
        CreateUserRequest request = CreateUserRequest.builder()
                .email("test@email.com")
                .password("password")
                .firstName("John")
                .lastName("Doe")
                .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("hashed");

        boolean result = userService.register(request);

        assertTrue(result);
        ArgumentCaptor<UserProfile> captor = ArgumentCaptor.forClass(UserProfile.class);
        verify(userRepository).save(captor.capture());
        assertEquals("test@email.com", captor.getValue().getEmail());
        assertEquals("hashed", captor.getValue().getPassword());
    }

    @Test
    void register_shouldThrowException_whenEmailExists() {
        CreateUserRequest request = CreateUserRequest.builder()
                .email("duplicate@email.com")
                .password("pass")
                .firstName("A")
                .lastName("B")
                .build();
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);


        assertThrows(DuplicateEmailException.class, () -> userService.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void getById_shouldReturnUser_whenExists() {
        UserProfile user = UserProfile.builder()
                .id(1L)
                .email("test@a.com")
                .firstName("John")
                .lastName("Doe")
                .build();
        when(userRepository.getUserProfileById(1L)).thenReturn(Optional.of(user));

        UserDTO dto = userService.getById(1L);

        assertNotNull(dto);
        assertEquals("test@a.com", dto.getEmail());
    }

    @Test
    void getById_shouldThrow_whenNotFound() {
        when(userRepository.getUserProfileById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getById(99L));
    }

    @Test
    void update_shouldUpdateUserData_whenFound() {
        UpdateUserDataRequest request = UpdateUserDataRequest.builder()
                .email("john@doe.com")
                .firstName("Johnny")
                .lastName(null)
                .dateOfBirth(
                        LocalDate.of(1990, 1, 1)
                )
                .gender(null)
                .heightCm(180)
                .weightKg(80.5)
                .build();


        UserProfile user = UserProfile.builder()
                .email("john@doe.com")
                .firstName("John")
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        UserDTO updated = userService.update(request);

        assertNotNull(updated);
        assertEquals("Johnny", updated.getFirstName());
        verify(userRepository).save(user);
    }

    @Test
    void update_shouldThrow_whenUserNotFound() {
        UpdateUserDataRequest request = UpdateUserDataRequest.builder()
                .email("not@found.com")
                .firstName("X")
                .lastName("Y")
                .dateOfBirth(null)
                .gender(null)
                .heightCm(null)
                .weightKg(null)
                .build();
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.update(request));
    }
}
