package github.keshaparrot.fitnesshelper.services.interfaces;

import github.keshaparrot.fitnesshelper.domain.dto.CreateUserRequest;
import github.keshaparrot.fitnesshelper.domain.dto.UpdateUserDataRequest;
import github.keshaparrot.fitnesshelper.domain.dto.UserDTO;
import github.keshaparrot.fitnesshelper.domain.entity.UserProfile;

public interface UserService {
    UserDTO getById(Long id);
    UserProfile getEntityById(Long id);
    UserDTO update (String userEmail,UpdateUserDataRequest request);
}
