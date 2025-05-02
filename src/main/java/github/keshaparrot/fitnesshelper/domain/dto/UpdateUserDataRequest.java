package github.keshaparrot.fitnesshelper.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class UpdateUserDataRequest {

    @NotNull(message = "email cannot be empty")
    private String email;

    private String firstName;
    private String lastName;

    private LocalDate dateOfBirth;

    private String gender;

    private Integer heightCm;
    private Double weightKg;
}
