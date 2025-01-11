package com.felipefranke.jira_incidents.api.user;

import com.felipefranke.jira_incidents.api.authentication.AuthenticateHeader;
import java.time.ZonedDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/user", produces = "application/xml")
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
    public ResponseEntity<CreatedUser> createNewUser(@RequestBody UserRequest userRequest) {
        User createdUser = userService.saveUser(userRequest);

        UserResponse userResponse = new UserResponse(
            createdUser.getId(),
            createdUser.getName(),
            createdUser.getEmailAddress(),
            createdUser.isActive()
        );

        ZonedDateTime time = ZonedDateTime.now();
        CreatedUser response = new CreatedUser(time, userResponse);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @UserActivityCheck
    @AuthenticateHeader
    @DeleteMapping(path = "/deactivate/{userId}")
    public ResponseEntity<Void> deactivateUserById(
        @PathVariable Long userId,
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
    public ResponseEntity<UserSuccessfullyRenewedCredentials> renewCredentials(
        @PathVariable Long userId,
        @RequestHeader("Authorization") String authorizationHeader,
        @RequestHeader("Accept") String acceptHeader
    ) {
        ZonedDateTime time = ZonedDateTime.now();
        userService.createNewCredentials(userId);
        UserSuccessfullyRenewedCredentials response = new UserSuccessfullyRenewedCredentials(userId, "new credentials sent by email", time);
        return ResponseEntity.ok(response);
    }
}
