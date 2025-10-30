package com.practice.service;

import com.practice.domain.User;

public interface UserService {

    public User getUser(long id);

    public User createUser(User user);

    public User deleteUser(long id);

    public User updateUser(long id, User info);
}
