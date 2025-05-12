package github.keshaparrot.fitnesshelper.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import github.keshaparrot.fitnesshelper.domain.dto.CreateUserRequest;
import github.keshaparrot.fitnesshelper.domain.dto.LoginRequest;
import github.keshaparrot.fitnesshelper.domain.dto.UserDTO;
import github.keshaparrot.fitnesshelper.domain.entity.UserProfile;
import github.keshaparrot.fitnesshelper.domain.mappers.UserMapper;
import github.keshaparrot.fitnesshelper.repository.UserRepository;
import github.keshaparrot.fitnesshelper.utils.exceptions.DuplicateEmailException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AuthServiceImpl authService;

    private CreateUserRequest createRequest;
    private LoginRequest loginRequest;
    private UserProfile sampleUser;
    private UserDTO sampleDto;

    @BeforeEach
    void setUp() {
        createRequest = CreateUserRequest.builder()
        .email("john.doe@example.com")
        .password("password123")
        .firstName("John")
        .lastName("Doe")
        .build();

        loginRequest = new LoginRequest();
        loginRequest.setEmail("john.doe@example.com");
        loginRequest.setPassword("password123");

        sampleUser = UserProfile.builder()
                .id(1L)
                .email("john.doe@example.com")
                .password("encodedPass")
                .firstName("John")
                .lastName("Doe")
                .build();

        sampleDto = new UserDTO();
        sampleDto.setId(1L);
        sampleDto.setEmail("john.doe@example.com");
        sampleDto.setFirstName("John");
        sampleDto.setLastName("Doe");
    }

    @Test
    @DisplayName("register successfully creates user when email not exists")
    void registerSuccess() {
        when(userRepository.existsByEmail(createRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(createRequest.getPassword())).thenReturn("encodedPass");
        when(userRepository.save(any(UserProfile.class))).thenAnswer(invocation -> {
            UserProfile u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(userMapper.toDto(any(UserProfile.class))).thenReturn(sampleDto);

        UserDTO result = authService.register(createRequest);

        ArgumentCaptor<UserProfile> captor = ArgumentCaptor.forClass(UserProfile.class);
        verify(userRepository).save(captor.capture());
        UserProfile saved = captor.getValue();
        assertEquals("john.doe@example.com", saved.getEmail());
        assertEquals("encodedPass", saved.getPassword());
        assertEquals("John", saved.getFirstName());
        assertEquals("Doe", saved.getLastName());

        assertEquals(sampleDto, result);
        verify(userMapper).toDto(saved);
    }

    @Test
    @DisplayName("register throws DuplicateEmailException when email exists")
    void registerDuplicateEmail() {
        when(userRepository.existsByEmail(createRequest.getEmail())).thenReturn(true);

        assertThrows(DuplicateEmailException.class, () -> authService.register(createRequest));
        verify(userRepository).existsByEmail(createRequest.getEmail());
        verifyNoMoreInteractions(userRepository, passwordEncoder, userMapper);
    }

    @Test
    @DisplayName("login returns UserDTO when credentials are valid")
    void loginSuccess() {
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches(loginRequest.getPassword(), sampleUser.getPassword())).thenReturn(true);
        when(userMapper.toDto(sampleUser)).thenReturn(sampleDto);

        UserDTO result = authService.login(loginRequest);

        assertEquals(sampleDto, result);
        verify(userRepository).findByEmail(loginRequest.getEmail());
        verify(passwordEncoder).matches(loginRequest.getPassword(), sampleUser.getPassword());
        verify(userMapper).toDto(sampleUser);
    }

    @Test
    @DisplayName("login throws BadCredentialsException when email not found")
    void loginEmailNotFound() {
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> authService.login(loginRequest));
        verify(userRepository).findByEmail(loginRequest.getEmail());
        verifyNoMoreInteractions(passwordEncoder, userMapper);
    }

    @Test
    @DisplayName("login throws BadCredentialsException when password does not match")
    void loginPasswordMismatch() {
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches(loginRequest.getPassword(), sampleUser.getPassword())).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authService.login(loginRequest));
        verify(userRepository).findByEmail(loginRequest.getEmail());
        verify(passwordEncoder).matches(loginRequest.getPassword(), sampleUser.getPassword());
        verifyNoMoreInteractions(userMapper);
    }
}