package com.spyro.messenger.friends.dto;

import com.fasterxml.jackson.databind.ser.std.CollectionSerializer;
import com.google.gson.annotations.JsonAdapter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FriendsDto {
    @JsonAdapter(CollectionSerializer.class)
    private List<String> friendRequests;
}
