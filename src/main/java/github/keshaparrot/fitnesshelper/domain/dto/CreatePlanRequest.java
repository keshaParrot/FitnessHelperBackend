package github.keshaparrot.fitnesshelper.domain.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePlanRequest {

    private Long userId;

    @NotBlank(message = "Goal is required")
    private String goal;

    @NotBlank(message = "Activity level is required")
    private String activityLevel;

    @NotBlank(message = "Experience level is required")
    private String experienceLevel;

    private String medicalRestrictions;

    private String equipmentAvailable;

    @NotNull(message = "Sessions per week is required")
    @Min(value = 1, message = "Sessions per week must be at least 1")
    private Integer sessionsPerWeek;

    @NotNull(message = "Minutes per session is required")
    @Min(value = 1, message = "Minutes per session must be at least 1")
    private Integer minutesPerSession;

    private String dietaryPreferences;

    private String sleepQuality;
}
