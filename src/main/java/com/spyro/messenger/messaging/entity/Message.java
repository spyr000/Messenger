package com.spyro.messenger.messaging.entity;

import com.spyro.messenger.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "messages",
        indexes = {
                @Index(name = "message_chat_id_index", columnList = "chat_id"),
                @Index(name = "message_time_index", columnList = "sending_time")
        }
)
@Getter
@Setter
@ToString
public class Message {
    @Id
    @UuidGenerator
    @Column(name = "message_id", nullable = false)
    private String id;
    @OneToOne
    @JoinColumn(name = "sender_id")
    private User sender;
    @Column(name = "content")
    private String content;
    @Column(name = "sending_time")
    private LocalDateTime timestamp;
    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat chat;

    public Message(User sender, String content, Chat chat) {
        this.sender = sender;
        this.content = content;
        this.chat = chat;
        this.timestamp = LocalDateTime.now();
    }

    public Message() {
        this.timestamp = LocalDateTime.now();
    }
}
