package com.felipefranke.jira_incidents.api.user;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.felipefranke.jira_incidents.api.role.Role;
import com.felipefranke.jira_incidents.api.ticket.Ticket;
import com.felipefranke.jira_incidents.api.token.Token;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JacksonXmlRootElement
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 12)
    private String name;

    @Column
    private String password;

    @Column(unique = true)
    private String emailAddress;

    @Column(unique = true)
    @Builder.Default
    private UUID clientId = UUID.randomUUID();

    @Column(unique = true)
    @Builder.Default
    private UUID clientSecret = UUID.randomUUID();

    @Column
    @Builder.Default
    private boolean active = true;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "token_id", referencedColumnName = "id")
    private Token token;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ticket> tickets;

    boolean isValidLogin(String decodedPassword, String requestUsername) {
        byte[] decodedUserPasswordBytes = Base64.getDecoder().decode(this.getPassword());
        String decodedUserPasswordString = new String(decodedUserPasswordBytes, StandardCharsets.UTF_8);
        decodedUserPasswordString = decodedUserPasswordString.trim().toLowerCase();
        decodedPassword = decodedPassword.trim().toLowerCase();
        requestUsername = requestUsername.trim().toLowerCase();
        String username = this.getName().trim().toLowerCase();
        return decodedUserPasswordString.equals(decodedPassword) && username.equals(requestUsername);
    }
}
