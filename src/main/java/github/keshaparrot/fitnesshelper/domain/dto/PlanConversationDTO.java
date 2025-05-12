package github.keshaparrot.fitnesshelper.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanConversationDTO {
    private Long id;
    private Long userId;
    private String type;
    private String status;
    private String metadata;
    private LocalDateTime createdAt;
}
