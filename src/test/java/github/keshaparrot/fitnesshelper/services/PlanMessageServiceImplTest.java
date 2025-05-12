package github.keshaparrot.fitnesshelper.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import github.keshaparrot.fitnesshelper.domain.entity.PlanConversation;
import github.keshaparrot.fitnesshelper.domain.entity.PlanMessage;
import github.keshaparrot.fitnesshelper.domain.dto.PlanMessageDTO;
import github.keshaparrot.fitnesshelper.domain.enums.MessageRole;
import github.keshaparrot.fitnesshelper.domain.mappers.PlanMessageMapper;
import github.keshaparrot.fitnesshelper.repository.PlanMessageRepository;
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
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;

@ExtendWith(MockitoExtension.class)
class PlanMessageServiceImplTest {
    @Mock
    private PlanMessageRepository messageRepository;
    @Mock
    private PlanMessageMapper planMessageMapper;
    @InjectMocks
    private PlanMessageServiceImpl service;

    private PlanConversation conversation;
    private PlanMessage msg1;
    private PlanMessage msg2;
    private PlanMessageDTO dto1;
    private PlanMessageDTO dto2;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        conversation = new PlanConversation();
        conversation.setId(5L);

        msg1 = PlanMessage.builder()
                .conversation(conversation)
                .role(MessageRole.USER.name())
                .content("hello")
                .timestamp(LocalDateTime.now().minusMinutes(5))
                .build();
        msg2 = PlanMessage.builder()
                .conversation(conversation)
                .role(MessageRole.ASSISTANT.name())
                .content("world")
                .timestamp(LocalDateTime.now())
                .build();

        dto1 = new PlanMessageDTO();
        dto1.setRole(MessageRole.USER.name());
        dto1.setContent("hello");
        dto1.setTimestamp(msg1.getTimestamp());

        dto2 = new PlanMessageDTO();
        dto2.setRole(MessageRole.ASSISTANT.name());
        dto2.setContent("world");
        dto2.setTimestamp(msg2.getTimestamp());

        pageable = Pageable.unpaged();
    }

    @Test
    @DisplayName("add assigns conversation, role, content and saves message")
    void addShouldBuildAndSave() {
        when(messageRepository.save(any(PlanMessage.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PlanMessage result = service.add(conversation, MessageRole.USER, "test content");

        ArgumentCaptor<PlanMessage> captor = ArgumentCaptor.forClass(PlanMessage.class);
        verify(messageRepository).save(captor.capture());
        PlanMessage saved = captor.getValue();

        assertEquals(conversation, saved.getConversation());
        assertEquals(MessageRole.USER.name(), saved.getRole());
        assertEquals("test content", saved.getContent());
        assertNotNull(saved.getTimestamp());

        assertEquals(saved, result);
    }

    @Test
    @DisplayName("findByConversation returns page of mapped DTOs")
    void findByConversationShouldReturnDtoPage() {
        Page<PlanMessage> pageIn = new PageImpl<>(Arrays.asList(msg1, msg2));
        when(messageRepository.findByConversationIdOrderByTimestamp(5L, pageable)).thenReturn(pageIn);
        when(planMessageMapper.toDto(msg1)).thenReturn(dto1);
        when(planMessageMapper.toDto(msg2)).thenReturn(dto2);

        Page<PlanMessageDTO> result = service.findByConversation(5L, pageable);

        assertEquals(2, result.getTotalElements());
        assertEquals(dto1, result.getContent().get(0));
        assertEquals(dto2, result.getContent().get(1));
        verify(messageRepository).findByConversationIdOrderByTimestamp(5L, pageable);
    }
}
