package github.keshaparrot.fitnesshelper.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import github.keshaparrot.fitnesshelper.domain.dto.ChatMessage;
import github.keshaparrot.fitnesshelper.domain.dto.CreatePlanRequest;
import github.keshaparrot.fitnesshelper.domain.dto.PlanConversationDTO;
import github.keshaparrot.fitnesshelper.domain.dto.PlanMessageDTO;
import github.keshaparrot.fitnesshelper.domain.entity.PlanConversation;
import github.keshaparrot.fitnesshelper.domain.entity.PlanEntry;
import github.keshaparrot.fitnesshelper.domain.entity.PlanMessage;
import github.keshaparrot.fitnesshelper.domain.entity.UserProfile;
import github.keshaparrot.fitnesshelper.domain.enums.MessageRole;
import github.keshaparrot.fitnesshelper.domain.mappers.PlanConversationMapper;
import github.keshaparrot.fitnesshelper.domain.mappers.PlanMessageMapper;
import github.keshaparrot.fitnesshelper.services.interfaces.PlanConversationService;
import github.keshaparrot.fitnesshelper.services.interfaces.PlanEntryService;
import github.keshaparrot.fitnesshelper.services.interfaces.PlanMessageService;
import github.keshaparrot.fitnesshelper.utils.llm.LLMClient;
import github.keshaparrot.fitnesshelper.utils.llm.PromptBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class PlanServiceImplTest {

    @Mock
    private PlanConversationService conversationService;
    @Mock
    private PlanMessageService messageService;
    @Mock
    private PlanEntryService planEntryService;
    @Mock
    private PromptBuilder promptBuilder;
    @Mock
    private LLMClient llmClient;
    @Mock
    private PlanConversationMapper conversationMapper;
    @Mock
    private PlanMessageMapper messageMapper;
    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private PlanServiceImpl service;

    private UserProfile user;
    private CreatePlanRequest request;
    private PlanConversation conversation;
    private PlanMessage userMsg;
    private PlanMessage assistantMsg;
    private List<PlanEntry> entries;
    private PlanConversationDTO conversationDto;
    private PlanMessageDTO messageDto;

    @BeforeEach
    void setUp() throws Exception {
        user = new UserProfile();
        user.setId(1L);

        request = CreatePlanRequest.builder()
                .userId(user.getId())
                .goal("strength")
                .activityLevel("moderate")
                .experienceLevel("beginner")
                .sessionsPerWeek(3)
                .minutesPerSession(45)
                .build();

        conversation = new PlanConversation();
        conversation.setId(10L);
        conversation.setMessages(new ArrayList<>());
        conversation.setStatus("PENDING");

        userMsg = new PlanMessage();
        userMsg.setRole(MessageRole.USER.name());
        userMsg.setContent("prompt");

        assistantMsg = new PlanMessage();
        assistantMsg.setRole(MessageRole.ASSISTANT.name());
        assistantMsg.setContent("[]");

        entries = Collections.singletonList(new PlanEntry());

        conversationDto = new PlanConversationDTO();
        conversationDto.setId(conversation.getId());

        messageDto = new PlanMessageDTO();
        messageDto.setContent(assistantMsg.getContent());
    }

    @Test
    @DisplayName("createPlan: successful flow")
    void createPlanSuccess() throws Exception {
        when(conversationService.create(user, request)).thenReturn(conversation);
        when(promptBuilder.buildPrompt(request, user)).thenReturn("prompt");
        when(messageService.add(conversation, MessageRole.USER, "prompt")).thenReturn(userMsg);
        when(llmClient.generate(anyString(), eq("prompt"))).thenReturn("[]");
        when(objectMapper.readValue(eq("[]"), any(TypeReference.class))).thenReturn(entries);
        when(objectMapper.writeValueAsString(entries)).thenReturn("[]");
        when(messageService.add(conversation, MessageRole.ASSISTANT, "[]")).thenReturn(assistantMsg);
        when(conversationMapper.toDto(conversation)).thenReturn(conversationDto);

        PlanConversationDTO result = service.createPlan(user, request);

        assertEquals(conversationDto, result);
        assertEquals("COMPLETED", conversation.getStatus());
        assertTrue(conversation.getMessages().contains(userMsg));
        assertTrue(conversation.getMessages().contains(assistantMsg));

        verify(conversationService).create(user, request);
        verify(promptBuilder).buildPrompt(request, user);
        verify(messageService).add(conversation, MessageRole.USER, "prompt");
        verify(llmClient).generate(anyString(), eq("prompt"));
        verify(objectMapper).readValue(eq("[]"), any(TypeReference.class));
        verify(objectMapper).writeValueAsString(entries);
        verify(messageService).add(conversation, MessageRole.ASSISTANT, "[]");
        verify(conversationService).complete(conversation);
        verify(planEntryService).saveEntries(conversation, entries);
        verify(conversationMapper).toDto(conversation);
    }

    @Test
    @DisplayName("createPlan: serialization failure throws")
    void createPlanSerializationFailure() throws Exception {
        when(conversationService.create(user, request)).thenReturn(conversation);
        when(promptBuilder.buildPrompt(request, user)).thenReturn("prompt");
        when(messageService.add(conversation, MessageRole.USER, "prompt")).thenReturn(userMsg);
        when(llmClient.generate(anyString(), eq("prompt"))).thenReturn("[]");
        when(objectMapper.readValue(eq("[]"), any(TypeReference.class))).thenReturn(entries);
        when(objectMapper.writeValueAsString(entries)).thenThrow(JsonProcessingException.class);

        assertThrows(RuntimeException.class, () -> service.createPlan(user, request));

        verify(conversationService).create(user, request);
        verify(promptBuilder).buildPrompt(request, user);
        verify(messageService).add(conversation, MessageRole.USER, "prompt");
        verify(llmClient).generate(anyString(), eq("prompt"));
        verify(objectMapper).readValue(eq("[]"), any(TypeReference.class));
        verify(objectMapper).writeValueAsString(entries);
        verifyNoMoreInteractions(conversationService, planEntryService, conversationMapper);
    }

    @Test
    @DisplayName("sendMessage: successful assistant reply")
    void sendMessageSuccess() {
        PlanMessage prior = new PlanMessage();
        prior.setRole(MessageRole.USER.name());
        prior.setContent("hello");
        conversation.setMessages(new ArrayList<>(List.of(prior)));

        when(conversationService.getEntityById(10L)).thenReturn(conversation);
        when(messageService.add(conversation, MessageRole.USER, "hi")).thenReturn(prior);
        when(llmClient.chat(anyString(), eq(List.of(new ChatMessage("user", "hello"))))).thenReturn("response");
        when(messageService.add(conversation, MessageRole.ASSISTANT, "response")).thenReturn(assistantMsg);
        when(messageMapper.toDto(assistantMsg)).thenReturn(messageDto);

        PlanMessageDTO result = service.sendMessage(10L, "hi", user);

        assertEquals(messageDto, result);
        verify(conversationService).getEntityById(10L);
        verify(messageService).add(conversation, MessageRole.USER, "hi");
        verify(llmClient).chat(anyString(), eq(List.of(new ChatMessage("user", "hello"))));
        verify(messageService).add(conversation, MessageRole.ASSISTANT, "response");
        verify(messageMapper).toDto(assistantMsg);
    }

    @Test
    @DisplayName("sendMessage: conversation not found propagates exception")
    void sendMessageConversationNotFound() {
        when(conversationService.getEntityById(99L)).thenThrow(new IllegalArgumentException("Not found"));

        assertThrows(IllegalArgumentException.class, () -> service.sendMessage(99L, "msg", user));
        verify(conversationService).getEntityById(99L);
        verifyNoMoreInteractions(messageService, llmClient);
    }
}
