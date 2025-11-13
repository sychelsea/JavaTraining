package com.practice.dao.sql;

import com.practice.model.User;
import java.util.Optional;

public interface UserDao {
    Optional<User> find(Long id);
    Optional<User> findForUpdate(Long id); // with lock
    Optional<User> findByUsername(String username);
    User save(User u);
    int update(User u);
    int delete(Long id);
}
