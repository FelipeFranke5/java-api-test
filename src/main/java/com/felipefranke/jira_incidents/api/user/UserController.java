package com.felipefranke.jira_incidents.api.user;

import com.felipefranke.jira_incidents.api.authentication.AuthenticateHeader;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(path = "/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(path = "/login/")
    public ResponseEntity<Void> login(@RequestBody UserLoginRequest loginRequest) {
        if (userService.isLoginAuthenticationValid(loginRequest)) {
            return ResponseEntity.ok().build();
        }

        throw new UserLoginRequestException("invalid credentials");
    }

    @PostMapping(path = "/create/")
    public ResponseEntity<Void> createNewUser(@RequestBody UserRequest userRequest) {
        userService.saveUser(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @UserActivityCheck
    @AuthenticateHeader
    @DeleteMapping(path = "/deactivate/{userId}")
    public ResponseEntity<Void> deactivateUserById(
        @PathVariable UUID userId,
        @RequestHeader("Authorization") String authorizationHeader,
        @RequestHeader("Accept") String acceptHeader
    ) {
        User authenticationUser = this.userService.getOneUserById(userId);
        userService.deactivateUser(authenticationUser.getId());
        return ResponseEntity.noContent().build();
    }

    @UserActivityCheck
    @AuthenticateHeader
    @PatchMapping(path = "/renew/{userId}")
    public ResponseEntity<Void> renewCredentials(
        @PathVariable UUID userId,
        @RequestHeader("Authorization") String authorizationHeader,
        @RequestHeader("Accept") String acceptHeader
    ) {
        userService.createNewCredentials(userId);
        return ResponseEntity.noContent().build();
    }
}
