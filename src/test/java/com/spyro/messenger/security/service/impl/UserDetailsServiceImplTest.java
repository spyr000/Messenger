package com.spyro.messenger.security.service.impl;

import com.spyro.messenger.annotation.ConfigureTests;
import com.spyro.messenger.user.entity.Role;
import com.spyro.messenger.user.entity.User;
import com.spyro.messenger.user.repo.UserRepo;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ConfigureTests
class UserDetailsServiceImplTest {
    @Mock
    private UserRepo userRepo;
    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void loadUserByUsername() {
        var testUser = User.builder()
                .username("JohnDoe")
                .password("password")
                .role(Role.USER)
                .build();
        testUser.setActivated(true);
        testUser.setConfirmed(true);

        when(userRepo.findByUsername(eq(testUser.getUsername())))
                .thenReturn(Optional.of(testUser));
        var actual = userDetailsService.loadUserByUsername(testUser.getUsername());
        assertThat(
                actual.getUsername()
        ).isEqualTo(testUser.getUsername());
        assertThat(
                actual.getPassword()
        ).isEqualTo(testUser.getPassword());
        assertThat(
                actual.isEnabled()
        ).isEqualTo(testUser.isEnabled());
        assertThat(
                actual.getAuthorities().toArray()
        ).isEqualTo(
                testUser.getAuthorities().toArray()
        );
        verify(userRepo).findByUsername(eq(testUser.getUsername()));
    }
}