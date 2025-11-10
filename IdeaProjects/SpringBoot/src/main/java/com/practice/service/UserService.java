package com.practice.service;

import com.practice.model.User;

public interface UserService {

    public User getUser(Long id);

    public User createUser(User user);

    public User deleteUser(Long id);

    public User updateUser(Long id, User info);

    public User updateUserWithPessimisticLock(Long id, User body, long holdMillis);

    public User updateUserWithOptimisticLock(Long id, User body);
}
