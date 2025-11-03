package com.practice.exception;

import com.practice.model.User;

public class UserAlreadyExistsException extends RuntimeException {

    private final User user;

    public UserAlreadyExistsException(String msg) {
        super(msg);
        this.user = null;
    }

    public UserAlreadyExistsException(User user) {
        super("User with id " + user.getId() + " already exists");
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}