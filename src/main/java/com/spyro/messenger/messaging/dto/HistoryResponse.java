package com.spyro.messenger.messaging.dto;

import com.fasterxml.jackson.databind.ser.std.MapSerializer;
import com.google.gson.annotations.JsonAdapter;
import com.spyro.messenger.messaging.entity.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class HistoryResponse {
    @JsonAdapter(MapSerializer.class)
    private List<MessageResponse> messages;

    public HistoryResponse(List<Message> messages) {
        this.messages = new ArrayList<>();
        for (var message: messages) {
            this.messages.add(
                    new MessageResponse(
                            message.getSender().getUsername(),
                            message.getId(),
                            message.getContent(),
                            message.getTimestamp()
                    )
            );
        }
    }
}
