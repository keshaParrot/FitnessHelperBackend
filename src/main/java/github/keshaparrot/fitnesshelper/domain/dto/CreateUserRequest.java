package github.keshaparrot.fitnesshelper.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateUserRequest {
    @NotNull(message = "email cannot be empty")
    private String email;
    @NotNull(message = "password cannot be empty")
    private String password;

    private String firstName;
    private String lastName;
}
