package com.spyro.messenger.emailverification.entity;

import com.spyro.messenger.exceptionhandling.exception.BaseException;
import com.spyro.messenger.security.misc.ConfirmationTokenType;
import com.spyro.messenger.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.codec.digest.DigestUtils;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.http.HttpStatus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "confirmation_tokens",
        indexes = @Index(name = "conf_token_index", columnList = "token")
)
@Getter
@Setter
@NoArgsConstructor
public class ConfirmationToken {
    @Id
    @UuidGenerator
    @Column(name = "token_id", nullable = false)
    private String id;
    @Column(name = "token", unique = true)
    private String token;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private LocalDateTime createdDate;
    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;
    @Enumerated(EnumType.STRING)
    @Column(name = "token_type", nullable = false)
    private ConfirmationTokenType tokenType;
    @Column(name = "extra_field")
    private String extraField;

    public ConfirmationToken(User user, ConfirmationTokenType tokenType) throws IOException {
        this.user = user;
        this.tokenType = tokenType;
        this.createdDate = LocalDateTime.now();
        this.token = generateToken(user);
    }

    public ConfirmationToken(User user, ConfirmationTokenType tokenType, String extraField) throws IOException {
        this.user = user;
        this.tokenType = tokenType;
        this.extraField = extraField;
        this.createdDate = LocalDateTime.now();
        this.token = generateToken(user);
    }

    private static String generateToken(User user) throws IOException {
        var bos = new ByteArrayOutputStream();
        ObjectOutputStream os = null;
        try {
            os = new ObjectOutputStream(bos);
            os.writeObject(user.toString());
            os.writeObject(LocalDateTime.now());
        } catch (IOException e) {
            throw new BaseException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        } finally {
            assert os != null;
            os.close();
        }
        return DigestUtils.sha3_256Hex(bos.toByteArray());
    }
}