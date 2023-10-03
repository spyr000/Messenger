package com.spyro.messenger.user.service.impl;


import com.spyro.messenger.exceptionhandling.exception.EntityNotFoundException;
import com.spyro.messenger.user.entity.User;
import com.spyro.messenger.user.repo.repo.UserRepo;
import com.spyro.messenger.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;

    @Override
    public User getUser(String username) {
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(User.class));
    }

    @Override
    public boolean userExists(User user) {
        return userRepo.existsByUsername(user.getUsername());
    }

    @Override
    public void saveUser(User user) { userRepo.save(user);}
}
