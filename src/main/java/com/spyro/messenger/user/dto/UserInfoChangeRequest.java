package com.spyro.messenger.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.spyro.messenger.user.entity.AdditionalInfo;
import com.spyro.messenger.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoChangeRequest implements Serializable {
    @JsonIgnore
    private AdditionalInfoDto additionalInfo;
    private String firstName;
    private String lastName;
    @JsonProperty("additionalInfo")
    private void parseAdditionalInfo(Map<String,Object> additionalInfoMap) {
        this.additionalInfo = new AdditionalInfoDto(
                additionalInfoMap.get("bio").toString(),
                additionalInfoMap.get("status").toString(),
                additionalInfoMap.get("avatarBase64").toString()
        );
    }

    public User changeUser(User user) {
        if (this.firstName != null) user.setFirstName(this.firstName);
        if (this.lastName != null) user.setLastName(this.lastName);
        if (this.additionalInfo == null) return user;
        var info = user.getAdditionalInfo();
        if (info == null) info = new AdditionalInfo();
        if (this.additionalInfo.getBio() != null) info.setBio(this.additionalInfo.getBio() );
        if (this.additionalInfo.getStatus() != null) info.setStatus(this.additionalInfo.getStatus());
        if (this.additionalInfo.getAvatarBase64() != null) info.setAvatarBase64(this.additionalInfo.getAvatarBase64());
        user.setAdditionalInfo(info);
        return user;
    }
}