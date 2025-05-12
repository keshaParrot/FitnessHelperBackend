package github.keshaparrot.fitnesshelper.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;

@Entity
@Table(name = "plan_entry")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PlanEntry {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "conversation_id", nullable = false)
    private PlanConversation conversation;

    @JsonProperty("week")
    @Column(name = "week_number", nullable = false)
    private Integer weekNumber;

    @JsonProperty("day")
    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(columnDefinition = "TEXT")
    private String action;
}