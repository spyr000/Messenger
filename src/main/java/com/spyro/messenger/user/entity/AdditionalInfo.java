package com.spyro.messenger.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "additional_info")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class AdditionalInfo {
    @Builder
    public AdditionalInfo(String bio, String status, String avatarBase64, User user) {
        this.bio = bio;
        this.status = status;
        this.avatarBase64 = avatarBase64;
        this.user = user;
    }

    @JsonIgnore
    @Id
    @UuidGenerator
    @Column(name = "additional_info_id", nullable = false)
    private String id;
    @Column(name = "bio", nullable = false)
    private String bio;
    @Column(name = "status", nullable = false)
    private String status;
    @Column(name = "avatar_base64", nullable = false)
    private String avatarBase64;
    @ToString.Exclude
    @JsonIgnore
    @OneToOne(mappedBy = "additionalInfo", orphanRemoval = true)
    private User user;
}
