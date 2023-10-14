package com.spyro.messenger.messaging.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.spyro.messenger.serialization.serializer.LocalDateTimeSerializer;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageRequest {
    private String content;
}
