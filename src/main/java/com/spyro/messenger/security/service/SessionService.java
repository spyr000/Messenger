package com.spyro.messenger.security.service;

import com.spyro.messenger.security.entity.Session;
import com.spyro.messenger.user.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.function.Consumer;

public interface SessionService {
    Session getExistingOrSaveNew(User user, long checksum);

    boolean isActive(String sessionId, UserDetails userDetails, long checksum, Consumer<Session> checksumMismatchingHandler);
}
