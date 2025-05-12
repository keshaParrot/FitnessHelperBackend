package github.keshaparrot.fitnesshelper.services;

import github.keshaparrot.fitnesshelper.domain.dto.UpdateUserDataRequest;
import github.keshaparrot.fitnesshelper.domain.dto.UserDTO;
import github.keshaparrot.fitnesshelper.domain.entity.UserProfile;
import github.keshaparrot.fitnesshelper.domain.mappers.UserMapper;
import github.keshaparrot.fitnesshelper.repository.UserRepository;
import github.keshaparrot.fitnesshelper.services.interfaces.UserService;
import github.keshaparrot.fitnesshelper.utils.exceptions.UserNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDTO getById(Long id){
         UserProfile user = userRepository.getUserProfileById(id)
                .orElseThrow(()-> new UserNotFoundException(id));

        return toDto(user);
    }

    @Override
    public UserProfile getEntityById(Long id){
        return userRepository.getUserProfileById(id)
                .orElseThrow(()-> new UserNotFoundException(id));
    }

    @Override
    public UserDTO update(String userEmail,UpdateUserDataRequest request) {
        UserProfile user = userRepository.findByEmail(userEmail)
                .orElseThrow(()->new UserNotFoundException(request.getEmail()));

        if (request.getFirstName() != null){
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null){
            user.setFirstName(request.getLastName());
        }
        if (request.getDateOfBirth() != null){
            user.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getGender() != null){
            user.setGender(request.getGender());
        }
        if (request.getHeightCm() != null){
            user.setHeightCm(request.getHeightCm());
        }
        if (request.getWeightKg() != null){
            user.setWeightKg(request.getWeightKg());
        }

        return toDto(userRepository.save(user));
    }

    private UserDTO toDto(UserProfile userProfile){
        return userMapper.toDto(userProfile);
    }
}
