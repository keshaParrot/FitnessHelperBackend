package github.keshaparrot.fitnesshelper.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import github.keshaparrot.fitnesshelper.domain.dto.CreatePlanRequest;
import github.keshaparrot.fitnesshelper.domain.dto.PlanConversationDTO;
import github.keshaparrot.fitnesshelper.domain.entity.PlanConversation;
import github.keshaparrot.fitnesshelper.domain.entity.UserProfile;
import github.keshaparrot.fitnesshelper.domain.enums.ConversationStatus;
import github.keshaparrot.fitnesshelper.domain.mappers.PlanConversationMapper;
import github.keshaparrot.fitnesshelper.repository.PlanConversationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class PlanConversationServiceImplTest {
    @Mock
    private PlanConversationRepository conversationRepository;

    @Mock
    private PlanConversationMapper mapper;

    @InjectMocks
    private PlanConversationServiceImpl service;

    private UserProfile user;
    private CreatePlanRequest request;
    private PlanConversation conversation;
    private PlanConversationDTO dto;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        user = new UserProfile();
        user.setId(42L);

        request = CreatePlanRequest.builder()
                .userId(user.getId())
                .goal("strength")
                .activityLevel("moderate")
                .experienceLevel("beginner")
                .sessionsPerWeek(3)
                .minutesPerSession(60)
                .build();

        conversation = PlanConversation.builder()
                .id(100L)
                .user(user)
                .type(request.getGoal())
                .status(ConversationStatus.PENDING.name())
                .metadata(request.toString())
                .messages(Collections.emptyList())
                .createdAt(LocalDateTime.now())
                .build();

        dto = PlanConversationDTO.builder()
                .id(conversation.getId())
                .userId(user.getId())
                .type(conversation.getType())
                .status(conversation.getStatus())
                .metadata(conversation.getMetadata())
                .createdAt(conversation.getCreatedAt())
                .build();

        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("create returns saved PlanConversation")
    void createShouldSaveAndReturn() {
        when(conversationRepository.save(any(PlanConversation.class))).thenReturn(conversation);

        PlanConversation result = service.create(user, request);

        ArgumentCaptor<PlanConversation> captor = ArgumentCaptor.forClass(PlanConversation.class);
        verify(conversationRepository).save(captor.capture());
        PlanConversation saved = captor.getValue();

        assertEquals(user, saved.getUser());
        assertEquals(request.getGoal(), saved.getType());
        assertEquals(ConversationStatus.PENDING.name(), saved.getStatus());
        assertEquals(request.toString(), saved.getMetadata());
        assertNotNull(saved.getCreatedAt());

        assertEquals(conversation, result);
    }

    @Test
    @DisplayName("complete sets status to COMPLETED and saves")
    void completeShouldUpdateStatusAndSave() {
        conversation.setStatus(ConversationStatus.PENDING.name());

        service.complete(conversation);

        assertEquals(ConversationStatus.COMPLETED.name(), conversation.getStatus());
        verify(conversationRepository).save(conversation);
    }

    @Test
    @DisplayName("findForUser returns page of DTOs")
    void findForUserShouldReturnDtoPage() {
        Page<PlanConversation> page = new PageImpl<>(List.of(conversation), pageable, 1);
        when(conversationRepository.findByUser_Id(user.getId(), pageable)).thenReturn(page);
        when(mapper.toDto(conversation)).thenReturn(dto);

        Page<PlanConversationDTO> result = service.findForUser(user.getId(), pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(dto, result.getContent().get(0));
    }

    @Test
    @DisplayName("getById returns DTO when exists")
    void getByIdSuccess() {
        when(conversationRepository.findById(conversation.getId())).thenReturn(Optional.of(conversation));
        when(mapper.toDto(conversation)).thenReturn(dto);

        PlanConversationDTO result = service.getById(conversation.getId());

        assertEquals(dto, result);
    }

    @Test
    @DisplayName("getById throws IllegalArgumentException when not found")
    void getByIdNotFound() {
        when(conversationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.getById(999L));
    }

    @Test
    @DisplayName("getEntityById returns entity when exists")
    void getEntityByIdSuccess() {
        when(conversationRepository.findById(conversation.getId())).thenReturn(Optional.of(conversation));

        PlanConversation result = service.getEntityById(conversation.getId());

        assertEquals(conversation, result);
    }

    @Test
    @DisplayName("getEntityById throws IllegalArgumentException when not found")
    void getEntityByIdNotFound() {
        when(conversationRepository.findById(888L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.getEntityById(888L));
    }
}
