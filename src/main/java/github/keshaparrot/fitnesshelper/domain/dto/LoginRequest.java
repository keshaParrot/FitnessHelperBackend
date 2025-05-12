package github.keshaparrot.fitnesshelper.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "should to be valid email address")
    private String email;
    @NotBlank(message = "Password cannot be empty")
    private String password;
}
