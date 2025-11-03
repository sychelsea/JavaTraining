package com.practice.service;

import com.practice.model.User;

public interface UserService {

    public User getUser(long id);

    public User createUser(User user);

    public User deleteUser(long id);

    public User updateUser(long id, User info);

    public User updateUserWithPessimisticLock(long id, User body, long holdMillis);

    public User updateUserWithOptimisticLock(long id, User body);
}
