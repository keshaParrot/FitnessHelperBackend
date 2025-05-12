package github.keshaparrot.fitnesshelper.services;

import github.keshaparrot.fitnesshelper.domain.dto.CreateUserRequest;
import github.keshaparrot.fitnesshelper.domain.dto.UserDTO;
import github.keshaparrot.fitnesshelper.domain.dto.LoginRequest;
import github.keshaparrot.fitnesshelper.domain.entity.UserProfile;
import github.keshaparrot.fitnesshelper.domain.mappers.UserMapper;
import github.keshaparrot.fitnesshelper.repository.UserRepository;
import github.keshaparrot.fitnesshelper.services.interfaces.AuthService;
import github.keshaparrot.fitnesshelper.utils.exceptions.DuplicateEmailException;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UserDTO register(CreateUserRequest request) {
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
        return null;
    }

    @Override
    public UserDTO login(LoginRequest request) {
        UserProfile user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }
        return toDto(user);
    }

    private UserDTO toDto(UserProfile userProfile){
        return userMapper.toDto(userProfile);
    }
}
