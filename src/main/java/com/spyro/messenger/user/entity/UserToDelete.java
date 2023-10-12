package com.spyro.messenger.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name="users_to_delete")
@Getter
@Setter
@NoArgsConstructor
public class UserToDelete {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_to_delete_id", nullable = false)
    private String id;
    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;
    @Column(name = "deactivation_time", nullable = false)
    private LocalDateTime deactivationTime;

    @Column(name = "deleting_time", nullable = false)
    private LocalDateTime deletingTime;

    public UserToDelete(User user, long millis) {
        var now = LocalDateTime.now();
        this.user = user;
        this.deactivationTime = now;
        this.deletingTime = now.plus(millis, ChronoUnit.MILLIS);
    }
}
