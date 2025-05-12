package github.keshaparrot.fitnesshelper.services.interfaces;

import github.keshaparrot.fitnesshelper.domain.dto.CreatePlanRequest;
import github.keshaparrot.fitnesshelper.domain.dto.PlanConversationDTO;
import github.keshaparrot.fitnesshelper.domain.entity.PlanConversation;
import github.keshaparrot.fitnesshelper.domain.entity.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PlanConversationService {

    PlanConversation create(UserProfile user, CreatePlanRequest request);
    void complete(PlanConversation conversation);
    Page<PlanConversationDTO> findForUser(Long userId, Pageable pageable);

    PlanConversationDTO getById(Long conversationId);

    PlanConversation getEntityById(Long conversationId);
}
