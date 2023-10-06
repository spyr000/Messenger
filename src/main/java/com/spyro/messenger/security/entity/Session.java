package com.spyro.messenger.security.entity;

import com.spyro.messenger.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="active_sessions", uniqueConstraints = {@UniqueConstraint(columnNames = {"username", "checksum"})})
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "session_id", nullable = false)
    private String id;
    @OneToOne
    @JoinColumn(name = "username", nullable = false, referencedColumnName = "username")
    private User user;

    @Column(name = "checksum", nullable = false)
    private Long checksum;

    public Session(User user, long checksum) {
        this.user = user;
        this.checksum = checksum;
    }
}
