package github.keshaparrot.fitnesshelper.services.interfaces;

import github.keshaparrot.fitnesshelper.domain.dto.CreateUserRequest;
import github.keshaparrot.fitnesshelper.domain.dto.UserDTO;
import github.keshaparrot.fitnesshelper.domain.dto.LoginRequest;

public interface AuthService {
    UserDTO register(CreateUserRequest request);
    UserDTO login(LoginRequest request);
}
