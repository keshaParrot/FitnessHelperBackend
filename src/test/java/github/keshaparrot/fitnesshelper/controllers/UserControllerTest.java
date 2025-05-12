package github.keshaparrot.fitnesshelper.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import github.keshaparrot.fitnesshelper.config.SecurityConfig;
import github.keshaparrot.fitnesshelper.domain.dto.UpdateUserDataRequest;
import github.keshaparrot.fitnesshelper.domain.dto.UserDTO;
import github.keshaparrot.fitnesshelper.domain.entity.UserProfile;
import github.keshaparrot.fitnesshelper.services.interfaces.UserService;
import github.keshaparrot.fitnesshelper.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private UserProfile sampleUser;
    private UserDTO sampleDto;

    @BeforeEach
    void setUp() {
        sampleUser = UserProfile.builder()
                .id(1L)
                .email("user@example.com")
                .password("password")
                .build();

        sampleDto = UserDTO.builder()
                .id(1L)
                .email("user@example.com")
                .firstName("John")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .gender("MALE")
                .heightCm(180)
                .weightKg(75.0)
                .build();

        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("password", "password"))
                .thenReturn(true);
    }

    @Test
    @DisplayName("GET /get-by-id with basic auth returns user DTO")
    void getByIdWithAuth() throws Exception {
        when(userService.getById(1L)).thenReturn(sampleDto);

        mockMvc.perform(get("/api/v1/user/get-by-id")
                        .with(httpBasic("user@example.com", "password"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("user@example.com"));

        verify(userService).getById(1L);
    }

    @Test
    @DisplayName("GET /get-by-id without auth returns 401")
    void getByIdWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/v1/user/get-by-id"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(userService);
    }

    @Test
    @DisplayName("PUT /update with basic auth and valid body returns updated DTO")
    void updateWithAuthValidBody() throws Exception {
        UpdateUserDataRequest req = UpdateUserDataRequest.builder()
                .email("user@example.com")
                .firstName("Jane")
                .lastName("Smith")
                .dateOfBirth(LocalDate.of(1985, 5, 20))
                .gender("FEMALE")
                .heightCm(170)
                .weightKg(65.0)
                .build();

        when(userService.update(eq("user@example.com"), any(UpdateUserDataRequest.class)))
                .thenReturn(sampleDto);

        mockMvc.perform(put("/api/v1/user/update")
                        .with(httpBasic("user@example.com", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@example.com"));

        verify(userService).update(eq("user@example.com"), any(UpdateUserDataRequest.class));
    }

    @Test
    @DisplayName("PUT /update with basic auth and invalid body returns 400")
    void updateWithAuthInvalidBody() throws Exception {
        UpdateUserDataRequest req = UpdateUserDataRequest.builder()
                .email("user@example.com")
                .firstName("")
                .lastName("")
                .build();


        mockMvc.perform(put("/api/v1/user/update")
                        .with(httpBasic("user@example.com", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.firstName").exists());

        verifyNoInteractions(userService);
    }

    @Test
    @DisplayName("PUT /update without auth returns 401")
    void updateWithoutAuth() throws Exception {
        UpdateUserDataRequest req = UpdateUserDataRequest.builder()
                .email("user@example.com")
                .build();

        mockMvc.perform(put("/api/v1/user/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                )
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(userService);
    }
}
