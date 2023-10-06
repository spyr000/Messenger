package com.spyro.messenger.user.service;


import com.spyro.messenger.user.entity.User;

public interface UserService {
    User getUser(String username);
    boolean userExists(User user);
    void save(User user);
}
