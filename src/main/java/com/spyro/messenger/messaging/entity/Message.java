package com.spyro.messenger.messaging.entity;

import com.spyro.messenger.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Getter
@Setter
@ToString
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "message_id", nullable = false)
    private String id;
    @OneToOne
    @JoinColumn(name = "sender_id")
    private User sender;
    @Column(name = "content")
    private String content;

    @Column(name = "sending_time")
    private LocalDateTime timestamp;
    private boolean received;
    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat chat;

    public Message(User sender, String content, Chat chat) {
        this.sender = sender;
        this.content = content;
        this.chat = chat;
        this.timestamp = LocalDateTime.now();
        this.received = false;
    }

    public Message() {
        this.timestamp = LocalDateTime.now();
    }
}
