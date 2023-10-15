package com.spyro.messenger.user.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.minidev.json.annotate.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@Entity
@Table(
        name = "users",
        indexes = {
                @Index(name = "user_username_index", columnList = "username", unique = true)
        }
)
@Getter
@Setter
@ToString
public class User implements Serializable, UserDetails {
    @Builder
    public User(String email,
                String password,
                String username,
                String firstName,
                String lastName,
                Role role
    ) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.restrictions = new UserRestrictions();
        this.confirmed = false;
        this.activated = true;
    }

    public User(String email,
                String password,
                String username,
                String firstName,
                String lastName
    ) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.restrictions = new UserRestrictions();
        this.confirmed = false;
        this.activated = true;
    }

    public User() {
        this.confirmed = false;
    }

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id", nullable = false)
    private Long id;
    @Column(name = "username", unique = true, nullable = false)
    private String username;
    @JsonIgnore
    @Column(name = "email", unique = true, nullable = false)
    @Email(message = "Email is not valid")
    private String email;
    @JsonIgnore
    @ToString.Exclude
    @Column(name = "password", nullable = false)
    @Size(min = 8, max = 22, message = "Password length should be between 8 an 22 characters")
    private String password;
    @Column(name = "user_first_name", nullable = false)
    private String firstName;
    @Column(name = "user_last_name", nullable = false)
    private String lastName;
    @JsonIgnore
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;
    @JsonIgnore
    @ToString.Exclude
    @Column(name = "confirmed")
    private boolean confirmed;
    @JsonIgnore
    @ToString.Exclude
    @Column(name = "activated")
    private boolean activated;
    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "additional_info_id", unique = true)
    private AdditionalInfo additionalInfo;
    @Embedded
    private UserRestrictions restrictions;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.confirmed && this.activated;
    }
}
