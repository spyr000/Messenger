package com.spyro.messenger.messaging.entity;

import com.spyro.messenger.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Getter
@Setter
@ToString
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "message_id", nullable = false)
    private String id;

    @ManyToOne
    @JoinColumn(name = "chat_id", nullable = false)
    private ChatRoom chat;
    @OneToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;
    @OneToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;
    @Column(name = "content")
    private String content;

    @Column(name = "sending_time")
    private LocalDateTime timestamp;
    @Enumerated(EnumType.STRING)
//    @Column(name)
    private MessageStatus status;

    public ChatMessage(ChatRoom chat, User sender, User recipient, String content) {
        this.chat = chat;
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }

    public ChatMessage() {
        this.timestamp = LocalDateTime.now();
    }
}
