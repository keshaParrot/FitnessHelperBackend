package github.keshaparrot.fitnesshelper.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import github.keshaparrot.fitnesshelper.domain.dto.CreatePlanRequest;
import github.keshaparrot.fitnesshelper.domain.dto.PlanConversationDTO;
import github.keshaparrot.fitnesshelper.domain.dto.PlanEntryDTO;
import github.keshaparrot.fitnesshelper.domain.dto.PlanMessageDTO;
import github.keshaparrot.fitnesshelper.domain.entity.PlanConversation;
import github.keshaparrot.fitnesshelper.domain.entity.PlanMessage;
import github.keshaparrot.fitnesshelper.domain.entity.UserProfile;
import github.keshaparrot.fitnesshelper.domain.enums.MessageRole;
import github.keshaparrot.fitnesshelper.services.interfaces.PlanConversationService;
import github.keshaparrot.fitnesshelper.services.interfaces.PlanMessageService;
import github.keshaparrot.fitnesshelper.services.PlanEntryServiceImpl;
import github.keshaparrot.fitnesshelper.services.interfaces.PlanService;
import github.keshaparrot.fitnesshelper.repository.UserRepository;
import github.keshaparrot.fitnesshelper.config.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(PlanController.class)
@Import(SecurityConfig.class)
class PlanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private PlanService planService;

    @Mock
    private PlanConversationService conversationService;

    @Mock
    private PlanMessageService messageService;

    @Mock
    private PlanEntryServiceImpl entryService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserProfile sampleUser;
    private String basicUser = "user@example.com";
    private String basicPass = "password";

    @BeforeEach
    void setUp() {
        sampleUser = UserProfile.builder()
                .id(1L)
                .email(basicUser)
                .password(basicPass)
                .build();
        given(userRepository.findByEmail(basicUser)).willReturn(Optional.of(sampleUser));
        given(passwordEncoder.matches(basicPass, basicPass)).willReturn(true);
    }

    @Test
    @DisplayName("POST /api/v1/plans - createPlan returns PlanConversationDTO")
    void createPlan() throws Exception {
        CreatePlanRequest request = CreatePlanRequest.builder()
                .userId(sampleUser.getId())
                .goal("strength")
                .activityLevel("moderate")
                .experienceLevel("beginner")
                .sessionsPerWeek(3)
                .minutesPerSession(60)
                .build();
        PlanConversationDTO dto = PlanConversationDTO.builder().id(10L).build();
        given(planService.createPlan(eq(sampleUser), any(CreatePlanRequest.class))).willReturn(dto);

        mockMvc.perform(post("/api/v1/plans")
                        .with(httpBasic(basicUser, basicPass))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10));

        verify(planService).createPlan(eq(sampleUser), any(CreatePlanRequest.class));
    }

    @Test
    @DisplayName("POST /api/v1/plans/conversations/{id}/messages - sendMessage returns PlanMessageDTO")
    void sendMessage() throws Exception {
        PlanMessageDTO dto = new PlanMessageDTO();
        dto.setContent("reply");
        given(planService.sendMessage(5L, "hello", sampleUser)).willReturn(dto);

        mockMvc.perform(post("/api/v1/plans/conversations/5/messages")
                        .with(httpBasic(basicUser, basicPass))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("content", "hello")))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("reply"));

        verify(planService).sendMessage(5L, "hello", sampleUser);
    }

    @Test
    @DisplayName("POST /api/v1/plans/{id}/messages - addMessage returns PlanMessage")
    void addMessage() throws Exception {
        PlanConversation conv = new PlanConversation();
        conv.setId(7L);
        PlanMessage message = PlanMessage.builder()
                .role(MessageRole.USER.name())
                .content("msg")
                .timestamp(LocalDateTime.now())
                .build();
        given(conversationService.getEntityById(7L)).willReturn(conv);
        given(messageService.add(conv, MessageRole.USER, "msg")).willReturn(message);

        mockMvc.perform(post("/api/v1/plans/7/messages?role=USER")
                        .with(httpBasic(basicUser, basicPass))
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("msg")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.content").value("msg"));

        verify(conversationService).getEntityById(7L);
        verify(messageService).add(conv, MessageRole.USER, "msg");
    }

    @Test
    @DisplayName("GET /api/v1/plans/user/get-all - getUserPlans returns page of PlanConversationDTO")
    void getUserPlans() throws Exception {
        PlanConversationDTO dto = PlanConversationDTO.builder().id(3L).build();
        Pageable pageable = PageRequest.of(0, 10);
        Page<PlanConversationDTO> page = new PageImpl<>(Collections.singletonList(dto), pageable, 1);
        given(conversationService.findForUser(sampleUser.getId(), pageable)).willReturn(page);

        mockMvc.perform(get("/api/v1/plans/user/get-all?page=0&size=10")
                        .with(httpBasic(basicUser, basicPass))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(3));

        verify(conversationService).findForUser(sampleUser.getId(), pageable);
    }

    @Test
    @DisplayName("GET /api/v1/plans/{id} - getPlan returns PlanConversationDTO")
    void getPlan() throws Exception {
        PlanConversationDTO dto = PlanConversationDTO.builder().id(4L).build();
        given(conversationService.getById(4L)).willReturn(dto);

        mockMvc.perform(get("/api/v1/plans/4")
                        .with(httpBasic(basicUser, basicPass))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(4));

        verify(conversationService).getById(4L);
    }

    @Test
    @DisplayName("GET /api/v1/plans/{id}/messages - getMessages returns page of PlanMessageDTO")
    void getMessages() throws Exception {
        PlanMessageDTO dto = new PlanMessageDTO();
        dto.setContent("c");
        Pageable pageable = PageRequest.of(0, 10);
        Page<PlanMessageDTO> page = new PageImpl<>(Collections.singletonList(dto), pageable, 1);
        given(messageService.findByConversation(6L, pageable)).willReturn(page);

        mockMvc.perform(get("/api/v1/plans/6/messages?page=0&size=10")
                        .with(httpBasic(basicUser, basicPass))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].content").value("c"));

        verify(messageService).findByConversation(6L, pageable);
    }

    @Test
    @DisplayName("GET /api/v1/plans/{id}/entries - getEntriesMessages returns page of PlanEntryDTO")
    void getEntriesMessages() throws Exception {
        PlanEntryDTO dto = new PlanEntryDTO();
        dto.setAction("a");
        Pageable pageable = PageRequest.of(0, 10);
        Page<PlanEntryDTO> page = new PageImpl<>(Collections.singletonList(dto), pageable, 1);
        given(entryService.getAllByConversationId(8L, pageable)).willReturn(page);

        mockMvc.perform(get("/api/v1/plans/8/entries?page=0&size=10")
                        .with(httpBasic(basicUser, basicPass))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].action").value("a"));

        verify(entryService).getAllByConversationId(8L, pageable);
    }
}