package com.practice.exception;

import com.practice.model.User;

public class UserOptimisticLockingFailureException extends RuntimeException{
    private final User user;

    public UserOptimisticLockingFailureException(String msg) {
        super(msg);
        this.user = null;
    }

    public UserOptimisticLockingFailureException(User user) {
        super("Optimistic lock conflict on user id=" + user.getId());
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
