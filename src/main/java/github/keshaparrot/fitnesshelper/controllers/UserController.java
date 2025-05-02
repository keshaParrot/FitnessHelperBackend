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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/get-by/{id}")
    public ResponseEntity<UserDTO> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody CreateUserRequest request) { //TODO check is here OK @Valid
        boolean result = userService.register(request);
        return ResponseEntity.status(result
                ? HttpStatus.OK
                : HttpStatus.BAD_REQUEST
        ).body("User was created successfully");
    }

    @PutMapping("/update")
    public ResponseEntity<UserDTO> update(@Valid @RequestBody UpdateUserDataRequest request) { //TODO check is here OK @Valid
        UserDTO updatedUser = userService.update(request);
        return ResponseEntity.status(
                HttpStatus.OK
        ).body(updatedUser);
    }

}
