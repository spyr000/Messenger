package com.spyro.messenger.security.service.impl;

import com.spyro.messenger.security.entity.Session;
import com.spyro.messenger.security.repo.SessionRepo;
import com.spyro.messenger.security.service.SessionService;
import com.spyro.messenger.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionServiceImpl implements SessionService {
    private final SessionRepo sessionRepo;

    @Override
    public Session getExistingOrSaveNew(User user, long checksum) {
        var session = sessionRepo.findByUser_UsernameAndChecksum(user.getUsername(), checksum);
        if (session == null) {
            session = sessionRepo.save(new Session(user, checksum));
            log.info("Session %s saved".formatted(session.toString()));
        }
        return session;
    }

    @Override
    public boolean isActive(String sessionId, UserDetails userDetails, long checksum, Consumer<Session> checksumMismatchingHandler) {
        var session = sessionRepo.findById(sessionId);
        var flag = session != null;
        if (flag) handleChecksumMismatch(session, checksum, checksumMismatchingHandler);
        return flag &&
                session.getUser().getUsername()
                        .equals(userDetails.getUsername());
    }

    private void handleChecksumMismatch(Session session, long checksum, Consumer<Session> checksumMismatchingHandler) {
        if (isChecksumMatching(session, checksum)) checksumMismatchingHandler.accept(session);
    }

    private boolean isChecksumMatching(Session session, long checksum) {
        return session.getChecksum() != checksum;
    }

}
