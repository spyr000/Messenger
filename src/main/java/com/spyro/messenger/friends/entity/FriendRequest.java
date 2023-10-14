package com.spyro.messenger.friends.entity;

import com.spyro.messenger.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.jpa.domain.AbstractAuditable;

@Entity
@Table(name = "friend_requests", uniqueConstraints = {@UniqueConstraint(columnNames = {"sender_id", "recipient_id"})})
@Getter
@Setter
@NoArgsConstructor
public class FriendRequest {
    @Id
    @UuidGenerator
    @Column(name = "friend_request_id", unique = true, nullable = false)
    private String id;

    @OneToOne
    @JoinColumn(name = "sender_id")
    private User sender;
    @OneToOne
    @JoinColumn(name = "recipient_id")
    private User recipient;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_condition")
    private FriendRequestCondition condition;

    public boolean approve() {
        if (this.condition == FriendRequestCondition.SENT) {
            this.condition = FriendRequestCondition.APPROVED;
            return true;
        }
        return false;
    }

    public boolean deny() {
        if (this.condition == FriendRequestCondition.SENT) {
            this.condition = FriendRequestCondition.DENIED;
            return true;
        }
        return false;
    }

    public boolean reject() {
        if (this.condition != FriendRequestCondition.APPROVED) {
            return false;
        }
        this.condition = FriendRequestCondition.SENT;
        return true;
    }

    public boolean isDenied() {
        return this.condition == FriendRequestCondition.DENIED;
    }

    public FriendRequest(User sender, User recipient) {
        this.sender = sender;
        this.recipient = recipient;
        this.condition = FriendRequestCondition.SENT;
    }
}
