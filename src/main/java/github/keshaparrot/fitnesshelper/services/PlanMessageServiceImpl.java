package github.keshaparrot.fitnesshelper.services;

import github.keshaparrot.fitnesshelper.domain.dto.PlanMessageDTO;
import github.keshaparrot.fitnesshelper.domain.entity.PlanConversation;
import github.keshaparrot.fitnesshelper.domain.entity.PlanMessage;
import github.keshaparrot.fitnesshelper.domain.enums.MessageRole;
import github.keshaparrot.fitnesshelper.domain.mappers.PlanMessageMapper;
import github.keshaparrot.fitnesshelper.repository.PlanMessageRepository;
import github.keshaparrot.fitnesshelper.services.interfaces.PlanMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PlanMessageServiceImpl implements PlanMessageService {
    private final PlanMessageRepository messageRepository;
    private final PlanMessageMapper planMessageMapper;

    @Override
    public PlanMessage add(PlanConversation conversation, MessageRole role, String content) {
        PlanMessage msg = PlanMessage.builder()
                .conversation(conversation)
                .role(role.name())
                .content(content)
                .timestamp(LocalDateTime.now())
                .build();
        return messageRepository.save(msg);
    }

    @Override
    public Page<PlanMessageDTO> findByConversation(Long conversationId, Pageable pageable) {
        return messageRepository.findByConversationIdOrderByTimestamp(conversationId,pageable).map(this::toDto);
    }

    private PlanMessageDTO toDto(PlanMessage planMessage) {
        return planMessageMapper.toDto(planMessage);
    }
}
