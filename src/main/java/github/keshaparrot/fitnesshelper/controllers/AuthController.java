package github.keshaparrot.fitnesshelper.controllers;

import github.keshaparrot.fitnesshelper.domain.dto.CreateUserRequest;
import github.keshaparrot.fitnesshelper.domain.dto.UserDTO;
import github.keshaparrot.fitnesshelper.domain.dto.LoginRequest;
import github.keshaparrot.fitnesshelper.services.interfaces.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;


    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody CreateUserRequest request) {
        UserDTO result = authService.register(request);
        return ResponseEntity.status(HttpStatus.OK
        ).body(result);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@Valid @RequestBody LoginRequest request) {
        UserDTO dto = authService.login(request);
        return ResponseEntity.ok(dto);
    }
}
