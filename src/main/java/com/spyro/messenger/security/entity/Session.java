package com.spyro.messenger.security.entity;

import com.spyro.messenger.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(
        name = "active_sessions",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"username", "checksum"})},
        indexes = {
                @Index(name = "session_id_index", columnList = "session_id", unique = true),
                @Index(name = "session_username_index", columnList = "username")
        }
)
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Session {
    @Id
    @UuidGenerator
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
