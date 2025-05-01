package github.keshaparrot.fitnesshelper.domain.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserDTO {

    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String gender;
    private Integer heightCm;
    private Double weightKg;
}
