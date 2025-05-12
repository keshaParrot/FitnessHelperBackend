package github.keshaparrot.fitnesshelper.utils.llm;

import github.keshaparrot.fitnesshelper.domain.dto.CreatePlanRequest;
import github.keshaparrot.fitnesshelper.domain.entity.UserProfile;

public interface PromptBuilder {

    String serializeMetadata(CreatePlanRequest req);
    String buildPrompt(CreatePlanRequest r, UserProfile user);
}