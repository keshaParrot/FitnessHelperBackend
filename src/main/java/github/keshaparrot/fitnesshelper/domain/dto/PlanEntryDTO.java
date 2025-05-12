package github.keshaparrot.fitnesshelper.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlanEntryDTO {
    private Integer weekNumber;
    private DayOfWeek dayOfWeek;
    private String action;
}
