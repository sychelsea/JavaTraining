package com.practice.dao.sql;

import com.practice.model.User;
import java.util.Optional;

public interface UserDao {
    Optional<User> find(long id);
    Optional<User> findForUpdate(long id); // with lock

    int create(User u);         // return rows affected
    int update(User u);
    int delete(long id);
}
