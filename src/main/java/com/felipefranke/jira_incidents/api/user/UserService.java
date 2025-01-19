package com.felipefranke.jira_incidents.api.user;

import com.felipefranke.jira_incidents.api.role.RoleNotFoundException;
import com.felipefranke.jira_incidents.api.role.RoleRepository;
import com.felipefranke.jira_incidents.api.token.Token;
import java.util.List;
import java.util.UUID;

import jakarta.transaction.Transactional;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository repository;
    private final RoleRepository roleRepository;
    private final JavaMailSender mailSender;
    private final Environment environment;

    public UserService(
        UserRepository userRepository,
        JavaMailSender mailSender,
        RoleRepository roleRepository,
        Environment environment
    ) {
        this.repository = userRepository;
        this.mailSender = mailSender;
        this.roleRepository = roleRepository;
        this.environment = environment;
    }

    public User getOneUserById(UUID id) {
        return repository.findById(id).orElseThrow(UserNotFoundException::new);
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
        return new StringBuilder()
                .append("Hello, ").append(user.getName()).append("!")
                .append("\n\n")
                .append("You just created a new User to the Jira Incidents Tracker project.")
                .append("\n")
                .append("Please store your credentials safely, you will need them in order to perform Authentication.")
                .append("\n")
                .append("You will need to encode them together in Base64.")
                .append("\n").append("Example: '")
                .append(randomUUID1)
                .append(":")
                .append(randomUUID2)
                .append("'")
                .append("\n")
                .append("Result: '")
                .append(new UserCredentialsUtil(UUID.fromString(randomUUID1), UUID.fromString(randomUUID2)).encodeCredentials())
                .append("'")
                .append("\n\n")
                .append("Your Credentials:")
                .append("\n")
                .append("ClientId: ")
                .append(user.getClientId().toString())
                .append("\n")
                .append("ClientSecret: ")
                .append(user.getClientSecret().toString())
                .append("\n")
                .append("UserId: ")
                .append(user.getId());
    }

    public StringBuilder onCredentialsChangeMessage(User user) {
        String randomUUID1 = UUID.randomUUID().toString();
        String randomUUID2 = UUID.randomUUID().toString();

        return new StringBuilder().append("Hello, ")
                .append(user.getName()).append("!")
                .append("\n\n")
                .append("Your credentials have been updated.")
                .append("\n")
                .append("Please store your new credentials safely, you will need them in order to perform Authentication.")
                .append("\n")
                .append("You will need to encode them together in Base64.")
                .append("\n")
                .append("Example: '")
                .append(randomUUID1)
                .append(":")
                .append(randomUUID2)
                .append("'")
                .append("\n")
                .append("Result: '")
                .append(new UserCredentialsUtil(UUID.fromString(randomUUID1), UUID.fromString(randomUUID2)).encodeCredentials())
                .append("'")
                .append("\n\n")
                .append("Your new Credentials:")
                .append("\n")
                .append("ClientId: ")
                .append(user.getClientId().toString())
                .append("\n")
                .append("ClientSecret: ")
                .append(user.getClientSecret().toString())
                .append("\n")
                .append("UserId: ")
                .append(user.getId());
    }

    public void sendMailWithCredentials(User user, String subject, StringBuilder body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmailAddress());
        message.setSubject(subject);
        message.setText(body.toString());
        message.setFrom(environment.getProperty("spring.mail.username"));
        mailSender.send(message);
    }

    @Transactional
    public void saveUser(UserRequest userRequest) {
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
    }

    @Transactional
    public void createNewCredentials(UUID id) {
        UUID newClientId = UUID.randomUUID();
        UUID newClientSecret = UUID.randomUUID();
        User user = repository.findById(id).orElseThrow(UserNotFoundException::new);

        if (!user.isActive()) {
            throw new IllegalArgumentException("This user is not active.");
        }

        user.setClientId(newClientId);
        user.setClientSecret(newClientSecret);
        user.setToken(new Token(user));

        repository.save(user);
        StringBuilder emailBody = this.onCredentialsChangeMessage(user);
        this.sendMailWithCredentials(user, "JIRA TRACKER - CREDENTIALS UPDATE", emailBody);
    }

    @Transactional
    public void deactivateUser(UUID id) {
        User user = repository.findById(id).orElseThrow(UserNotFoundException::new);

        if (!user.isActive()) {
            throw new IllegalArgumentException("This user is already inactive");
        }

        user.setActive(false);
        repository.save(user);
    }

    public long getUsersCount() {
        return repository.count();
    }

    public boolean isLoginAuthenticationValid(UserLoginRequest loginRequest) {
        User user = repository.findByName(loginRequest.username()).orElseThrow(UserNotFoundException::new);
        return user.isValidLogin(loginRequest.password(), loginRequest.username());
    }
}
