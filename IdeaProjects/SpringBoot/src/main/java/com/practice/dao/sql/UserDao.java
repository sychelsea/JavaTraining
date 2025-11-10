package com.practice.dao.sql;

import com.practice.model.User;
import java.util.Optional;

public interface UserDao {
    Optional<User> find(Long id);
    Optional<User> findForUpdate(Long id); // with lock

    int create(User u);         // return rows affected
    int update(User u);
    int delete(Long id);
}
