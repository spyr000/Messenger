package com.spyro.messenger.messaging.entity;

import com.spyro.messenger.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(
        name = "chats",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"initiator_id", "member_id"})},
        indexes = @Index(name = "chat_index", columnList = "initiator_id, member_id", unique = true)
)
@Getter
@Setter
@NoArgsConstructor
public class Chat {
    @Id
    @UuidGenerator
    @Column(name = "chat_id", nullable = false)
    private String id;
    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;
    @ManyToOne
    @JoinColumn(name = "member_id")
    private User member;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "chat_id")
    private List<Message> messages = new ArrayList<>();

    public Chat(User initiator, User member) {
        this.initiator = initiator;
        this.member = member;
    }

    public void addMessage(Message message) {
        if (message == null) return;
        messages.add(message);
    }
}
