package com.felipefranke.jira_incidents.api.user;

import com.felipefranke.jira_incidents.api.role.RoleNotFoundException;
import com.felipefranke.jira_incidents.api.role.RoleRepository;
import com.felipefranke.jira_incidents.api.token.Token;
import com.felipefranke.jira_incidents.api.token.TokenService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository repository;
    private final RoleRepository roleRepository;
    private final TokenService tokenService;
    private final JavaMailSender mailSender;
    private final Environment environment;

    public UserService(
        UserRepository userRepository,
        JavaMailSender mailSender,
        RoleRepository roleRepository,
        TokenService tokenService,
        Environment environment
    ) {
        this.repository = userRepository;
        this.mailSender = mailSender;
        this.roleRepository = roleRepository;
        this.tokenService = tokenService;
        this.environment = environment;
    }

    public void validateTokenUsage(User user) {
        if (user.getToken().getUses() > 10) {
            throw new IllegalArgumentException("Your credentials have expired. Please create a new pair.");
        }

        this.tokenService.incrementUses(user.getToken());
    }

    public User getOneUserById(Long id) {
        Optional<User> user = repository.findById(id);

        if (user.isEmpty()) {
            throw new UserNotFoundException();
        }

        return user.get();
    }

    public List<User> getUserList() {
        return repository.findAll();
    }

    public void validateEmailUniqueness(String email) {
        if (repository.existsByEmailAddress(email.trim())) {
            throw new IllegalArgumentException("user with this email already exists");
        }
    }

    public StringBuilder onUserCreationMessage(User user) {
        String randomUUID1 = UUID.randomUUID().toString();
        String randomUUID2 = UUID.randomUUID().toString();

        StringBuilder bodyBuilder = new StringBuilder()
            .append("Hello, " + user.getName() + "!")
            .append("\n\n")
            .append("You just created a new User to the Jira Incidents Tracker project.")
            .append("\n")
            .append("Please store your credentials safely, you will need them in order to perform Authentication.")
            .append("\n")
            .append("You will need to encode them together in Base64.")
            .append("\n")
            .append("Example: '" + randomUUID1 + ":" + randomUUID2 + "'")
            .append("\n")
            .append(
                "Result: '" + new UserCredentialsUtil(UUID.fromString(randomUUID1), UUID.fromString(randomUUID2)).encodeCredentials() + "'"
            )
            .append("\n\n")
            .append("Your Credentials:")
            .append("\n")
            .append("ClientId: " + user.getClientId().toString())
            .append("\n")
            .append("ClientSecret: " + user.getClientSecret().toString());

        return bodyBuilder;
    }

    public StringBuilder onCredentialsChangeMessage(User user) {
        String randomUUID1 = UUID.randomUUID().toString();
        String randomUUID2 = UUID.randomUUID().toString();

        StringBuilder bodyBuilder = new StringBuilder()
            .append("Hello, " + user.getName() + "!")
            .append("\n\n")
            .append("Your credentials have been updated.")
            .append("\n")
            .append("Please store your new credentials safely, you will need them in order to perform Authentication.")
            .append("\n")
            .append("You will need to encode them together in Base64.")
            .append("\n")
            .append("Example: '" + randomUUID1 + ":" + randomUUID2 + "'")
            .append("\n")
            .append(
                "Result: '" + new UserCredentialsUtil(UUID.fromString(randomUUID1), UUID.fromString(randomUUID2)).encodeCredentials() + "'"
            )
            .append("\n\n")
            .append("Your new Credentials:")
            .append("\n")
            .append("ClientId: " + user.getClientId().toString())
            .append("\n")
            .append("ClientSecret: " + user.getClientSecret().toString());

        return bodyBuilder;
    }

    public void sendMailWithCredentials(User user, String subject, StringBuilder body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmailAddress());
        message.setSubject(subject);
        message.setText(body.toString());
        message.setFrom(environment.getProperty("spring.mail.username"));
        mailSender.send(message);
    }

    public User saveUser(UserRequest userRequest) {
        User user = new User();
        UserCredentialsUtil credentialsUtil = new UserCredentialsUtil(user.getClientId(), user.getClientSecret());

        user.setName(userRequest.username());
        user.setPassword(credentialsUtil.encodePassword(userRequest.password()));
        user.setToken(new Token(user));
        this.validateEmailUniqueness(userRequest.email());
        user.setEmailAddress(userRequest.email());
        user.setActive(true);
        user.setRole(roleRepository.findByName("USER").orElseThrow(RoleNotFoundException::new));
        repository.save(user);

        StringBuilder emailBody = this.onUserCreationMessage(user);
        this.sendMailWithCredentials(user, "JIRA TRACKER - NEW CREDENTIALS", emailBody);
        return user;
    }

    public void createNewCredentials(Long id) {
        UUID newClientId = UUID.randomUUID();
        UUID newClientSecret = UUID.randomUUID();

        Optional<User> user = repository.findById(id);

        if (user.isEmpty()) {
            throw new UserNotFoundException();
        }

        if (!user.get().isActive()) {
            throw new IllegalArgumentException("This user is not active.");
        }

        user.get().setClientId(newClientId);
        user.get().setClientSecret(newClientSecret);
        user.get().setToken(new Token(user.get()));
        repository.save(user.get());

        StringBuilder emailBody = this.onCredentialsChangeMessage(user.get());
        this.sendMailWithCredentials(user.get(), "JIRA TRACKER - CREDENTIALS UPDATE", emailBody);
    }

    public void deactivateUser(Long id) {
        Optional<User> user = repository.findById(id);

        if (user.isEmpty()) {
            throw new UserNotFoundException();
        }

        if (!user.get().isActive()) {
            throw new IllegalArgumentException("This user is already inactive.");
        }

        user.get().setActive(false);
        repository.save(user.get());
    }

    public long getUsersCount() {
        return repository.count();
    }

    public boolean isLoginAuthenticationValid(UserLoginRequest loginRequest) {
        User user = repository.findByName(loginRequest.username()).orElseThrow(() -> new UserNotFoundException());
        return user.isValidLogin(loginRequest.password(), loginRequest.username());
    }
}
