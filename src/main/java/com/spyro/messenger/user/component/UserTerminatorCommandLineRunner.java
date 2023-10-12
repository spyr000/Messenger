package com.spyro.messenger.user.component;

import com.spyro.messenger.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserTerminatorCommandLineRunner implements CommandLineRunner {
    private final UserService userService;
    @Override
    public void run(String... args) {
        userService.deleteAllUsersToDelete();
    }
}
