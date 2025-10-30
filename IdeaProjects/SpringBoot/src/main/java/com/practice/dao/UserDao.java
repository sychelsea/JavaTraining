package com.practice.dao;

import com.practice.domain.User;
import java.util.Optional;

public interface UserDao {
    Optional<User> find(long id);
    // boolean existsById(long id);
    int create(User u);         // return rows affected
    int update(User u);
    int delete(long id);
}
