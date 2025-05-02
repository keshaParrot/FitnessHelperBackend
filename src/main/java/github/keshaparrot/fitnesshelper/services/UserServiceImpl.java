package github.keshaparrot.fitnesshelper.services;

import github.keshaparrot.fitnesshelper.domain.dto.CreateUserRequest;
import github.keshaparrot.fitnesshelper.domain.dto.UpdateUserDataRequest;
import github.keshaparrot.fitnesshelper.domain.dto.UserDTO;
import github.keshaparrot.fitnesshelper.domain.entity.UserProfile;
import github.keshaparrot.fitnesshelper.domain.mappers.UserMapper;
import github.keshaparrot.fitnesshelper.repository.UserRepository;
import github.keshaparrot.fitnesshelper.services.interfaces.UserService;
import github.keshaparrot.fitnesshelper.utils.exceptions.DuplicateEmailException;
import github.keshaparrot.fitnesshelper.utils.exceptions.UserNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserDTO getById(Long id){
         UserProfile user = userRepository.getUserProfileById(id)
                .orElseThrow(()-> new UserNotFoundException(id));

        return entityToDto(user);
    }

    @Override
    public boolean register(CreateUserRequest request) {
        if(userRepository.existsByEmail(request.getEmail())){
            throw new DuplicateEmailException(request.getEmail());
        }

        UserProfile userProfile = UserProfile.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build();
        userRepository.save(userProfile);
        return true;
    }

    @Override
    public UserDTO update(UpdateUserDataRequest request) {
        UserProfile user = userRepository.findByEmail(request.getEmail())
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

        return entityToDto(userRepository.save(user));
    }

    private UserDTO entityToDto(UserProfile userProfile){
        return userMapper.toDto(userProfile);
    }
}
