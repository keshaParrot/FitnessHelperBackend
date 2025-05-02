package github.keshaparrot.fitnesshelper.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import github.keshaparrot.fitnesshelper.domain.dto.CreateUserRequest;
import github.keshaparrot.fitnesshelper.domain.dto.UpdateUserDataRequest;
import github.keshaparrot.fitnesshelper.domain.dto.UserDTO;
import github.keshaparrot.fitnesshelper.services.interfaces.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    private UserDTO mockUser;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public UserService userService() {
            return Mockito.mock(UserService.class);
        }
    }

    @BeforeEach
    void setup() {
        mockUser = UserDTO.builder()
                .email("john@doe.com")
                .firstName("John")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .gender("male")
                .heightCm(180)
                .weightKg(75.0)
                .build();
    }

    @Test
    void getById_shouldReturnUser() throws Exception {
        Mockito.when(userService.getById(1L)).thenReturn(mockUser);

        mockMvc.perform(get("/api/v1/user/get-by/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john@doe.com"))
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void register_shouldReturnOk_whenUserCreated() throws Exception {
        CreateUserRequest request = CreateUserRequest.builder()
                .email("new@user.com")
                .password("pass123")
                .firstName("New")
                .lastName("User")
                .build();

        Mockito.when(userService.register(any(CreateUserRequest.class))).thenReturn(true);

        mockMvc.perform(post("/api/v1/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("User was created successfully"));
    }

    @Test
    void register_shouldReturnBadRequest_whenFailed() throws Exception {
        CreateUserRequest request = CreateUserRequest.builder()
                .email("fail@user.com")
                .password("badpass")
                .firstName("Fail")
                .lastName("User")
                .build();

        Mockito.when(userService.register(any(CreateUserRequest.class))).thenReturn(false);

        mockMvc.perform(post("/api/v1/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User was created successfully"));
    }

    @Test
    void update_shouldReturnUpdatedUser() throws Exception {
        UpdateUserDataRequest request = UpdateUserDataRequest.builder()
                .email("john@doe.com")
                .firstName("Johnny")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .gender("male")
                .heightCm(180)
                .weightKg(75.0)
                .build();

        Mockito.when(userService.update(any(UpdateUserDataRequest.class))).thenReturn(mockUser);

        mockMvc.perform(put("/api/v1/user/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john@doe.com"))
                .andExpect(jsonPath("$.firstName").value("John"));
    }
}
