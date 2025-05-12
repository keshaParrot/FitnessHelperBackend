package github.keshaparrot.fitnesshelper.services;

import github.keshaparrot.fitnesshelper.domain.dto.CreatePlanRequest;
import github.keshaparrot.fitnesshelper.domain.dto.PlanConversationDTO;
import github.keshaparrot.fitnesshelper.domain.entity.PlanConversation;
import github.keshaparrot.fitnesshelper.domain.entity.UserProfile;
import github.keshaparrot.fitnesshelper.domain.enums.ConversationStatus;
import github.keshaparrot.fitnesshelper.domain.mappers.PlanConversationMapper;
import github.keshaparrot.fitnesshelper.repository.PlanConversationRepository;
import github.keshaparrot.fitnesshelper.services.interfaces.PlanConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class PlanConversationServiceImpl implements PlanConversationService {
    private final PlanConversationRepository conversationRepository;
    private final PlanConversationMapper mapper;

    @Override
    public PlanConversation create(UserProfile user, CreatePlanRequest request) {
        String conversationType = request.getGoal();
        String metadata = request.toString();

        PlanConversation conv = PlanConversation.builder()
                .user(user)
                .type(conversationType)
                .status(ConversationStatus.PENDING.name())
                .metadata(metadata)
                .messages(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .build();
        return conversationRepository.save(conv);
    }

    @Override
    public void complete(PlanConversation conversation) {
        conversation.setStatus(ConversationStatus.COMPLETED.name());
        conversationRepository.save(conversation);
    }

    @Override
    public Page<PlanConversationDTO> findForUser(Long userId, Pageable pageable) {
        return conversationRepository.findByUser_Id(userId, pageable).map(this::toDto);
    }

    @Override
    public PlanConversationDTO getById(Long conversationId) {
        return toDto(conversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found: " + conversationId)));
    }

    @Override
    public PlanConversation getEntityById(Long conversationId) {
        return conversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found: " + conversationId));
    }

    private PlanConversationDTO toDto(PlanConversation planConversation) {
        return mapper.toDto(planConversation);
    }
}
