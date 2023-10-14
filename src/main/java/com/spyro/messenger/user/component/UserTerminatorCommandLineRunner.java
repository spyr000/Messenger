package com.spyro.messenger.user.component;

import com.spyro.messenger.user.service.UserService;
import com.spyro.messenger.user.service.UserTerminatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserTerminatorCommandLineRunner implements CommandLineRunner {
    private final UserTerminatorService terminatorService;
    @Override
    public void run(String... args) {
        terminatorService.deleteAllUsersToDelete();
    }
}
