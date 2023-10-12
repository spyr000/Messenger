package com.spyro.messenger.user.dto;

import com.spyro.messenger.user.entity.AdditionalInfo;
import lombok.*;

import java.io.Serializable;

/**
 * DTO for {@link com.spyro.messenger.user.entity.AdditionalInfo}
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdditionalInfoDto implements Serializable {
    private String bio;
    private String status;
    private String avatarBase64;
    public static AdditionalInfoDto fromAdditionalInfo(AdditionalInfo additionalInfo) {
        return new AdditionalInfoDto(
                additionalInfo.getBio(),
                additionalInfo.getStatus(),
                additionalInfo.getAvatarBase64()
        );
    }
}