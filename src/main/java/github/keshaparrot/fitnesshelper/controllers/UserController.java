package github.keshaparrot.fitnesshelper.controllers;

import github.keshaparrot.fitnesshelper.domain.dto.CreateUserRequest;
import github.keshaparrot.fitnesshelper.domain.dto.UpdateUserDataRequest;
import github.keshaparrot.fitnesshelper.domain.dto.UserDTO;
import github.keshaparrot.fitnesshelper.domain.entity.UserProfile;
import github.keshaparrot.fitnesshelper.services.interfaces.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/get-by-id")
    public ResponseEntity<UserDTO> getById(@AuthenticationPrincipal UserProfile user) {
        return ResponseEntity.ok(userService.getById(user.getId()));
    }

    @PutMapping("/update")
    public ResponseEntity<UserDTO> update(
            @AuthenticationPrincipal UserProfile user,
            @Valid @RequestBody UpdateUserDataRequest request) {
        UserDTO updatedUser = userService.update(user.getEmail(),request);
        return ResponseEntity.status(
                HttpStatus.OK
        ).body(updatedUser);
    }

}
