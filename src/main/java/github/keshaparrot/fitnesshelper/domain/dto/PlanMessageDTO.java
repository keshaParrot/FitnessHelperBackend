package github.keshaparrot.fitnesshelper.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanMessageDTO {
    private Long id;
    private String role; // USER or ASSISTANT
    private String content;
    private LocalDateTime timestamp;
}
