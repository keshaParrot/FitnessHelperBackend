package github.keshaparrot.fitnesshelper.services.interfaces;

import github.keshaparrot.fitnesshelper.domain.dto.PlanMessageDTO;
import github.keshaparrot.fitnesshelper.domain.entity.PlanConversation;
import github.keshaparrot.fitnesshelper.domain.entity.PlanMessage;
import github.keshaparrot.fitnesshelper.domain.enums.MessageRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PlanMessageService {

    PlanMessage add(PlanConversation conversation, MessageRole role, String content);
    Page<PlanMessageDTO> findByConversation(Long conversationId, Pageable pageable);
}
