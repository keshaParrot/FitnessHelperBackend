package github.keshaparrot.fitnesshelper.services.interfaces;

import github.keshaparrot.fitnesshelper.domain.dto.CreateUserRequest;
import github.keshaparrot.fitnesshelper.domain.dto.UpdateUserDataRequest;
import github.keshaparrot.fitnesshelper.domain.dto.UserDTO;

public interface UserService {
    UserDTO getById(Long id);
    boolean register(CreateUserRequest request);
    UserDTO update (UpdateUserDataRequest request);
}
