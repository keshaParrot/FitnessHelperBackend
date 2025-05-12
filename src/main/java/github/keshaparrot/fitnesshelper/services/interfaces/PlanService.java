package github.keshaparrot.fitnesshelper.services.interfaces;

import github.keshaparrot.fitnesshelper.domain.dto.CreatePlanRequest;
import github.keshaparrot.fitnesshelper.domain.dto.PlanConversationDTO;
import github.keshaparrot.fitnesshelper.domain.dto.PlanMessageDTO;
import github.keshaparrot.fitnesshelper.domain.entity.PlanConversation;
import github.keshaparrot.fitnesshelper.domain.entity.PlanMessage;
import github.keshaparrot.fitnesshelper.domain.entity.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PlanService {
    @Transactional
    PlanConversationDTO createPlan(UserProfile user, CreatePlanRequest req);

    @Transactional
    PlanMessageDTO sendMessage(Long convId, String content, UserProfile user);
}
