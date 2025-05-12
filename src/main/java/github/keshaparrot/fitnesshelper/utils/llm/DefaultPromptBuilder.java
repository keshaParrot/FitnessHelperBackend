package github.keshaparrot.fitnesshelper.utils.llm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import github.keshaparrot.fitnesshelper.domain.dto.CreatePlanRequest;
import github.keshaparrot.fitnesshelper.domain.entity.UserProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class DefaultPromptBuilder implements PromptBuilder {
    private final ObjectMapper objectMapper;

    @Override
    public String serializeMetadata(CreatePlanRequest req) {
        try {
            return objectMapper.writeValueAsString(req);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize plan metadata", e);
        }
    }

    @Override
    public String buildPrompt(CreatePlanRequest req, UserProfile user) {
        StringBuilder sb = new StringBuilder();
        sb.append("User profile:\n")
                .append("Age: ").append(user.getAge()).append(", Gender: ").append(user.getGender()).append("\n")
                .append("Height: ").append(user.getHeightCm()).append(" cm, Weight: ").append(user.getWeightKg()).append(" kg\n")
                .append("Goal: ").append(req.getGoal())
                .append(", Activity level: ").append(req.getActivityLevel())
                .append(", Experience: ").append(req.getExperienceLevel()).append("\n")
                .append("Medical restrictions: ").append(req.getMedicalRestrictions()).append("\n")
                .append("Equipment: ").append(req.getEquipmentAvailable()).append("\n")
                .append("Sessions per week: ").append(req.getSessionsPerWeek())
                .append(", Minutes per session: ").append(req.getMinutesPerSession()).append("\n")
                .append("Dietary preferences: ").append(req.getDietaryPreferences()).append("\n")
                .append("Sleep quality: ").append(req.getSleepQuality()).append("\n\n");

        sb.append("Generate a 4-week fitness plan as a JSON array of exactly 28 objects. ")
                .append("Entries must be ordered by week: first 7 objects for week 1 (MONDAY→SUNDAY), ")
                .append("then 7 for week 2, then week 3, then week 4. ")
                .append("Each object should have fields: week (int 1–4), day (\"MONDAY\"…\"SUNDAY\"), ")
                .append("action (e.g. \"Push-ups: 3×12; Nutrition: High-protein breakfast\").\n\n");

        sb.append("Respond **only** with the complete JSON array—no markdown, no backticks, no ellipsis, no extra text:\n\n")
                .append("[\n")
                .append("  {\"week\":1, \"day\":\"MONDAY\",    \"action\":\"Push-ups: 3×12; Nutrition: High-protein breakfast\"},\n")
                .append("]\n\n");

        sb.append("Now generate the full 4-week plan with exactly 28 entries in that order.");

        return sb.toString();
    }
}