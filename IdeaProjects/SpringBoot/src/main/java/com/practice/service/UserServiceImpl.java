package com.practice.service;

import com.practice.dao.UserDao;
import com.practice.domain.User;
import com.practice.exception.UserAlreadyExistsException;
import com.practice.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Qualifier("jdbcUserService")
public class UserServiceImpl implements UserService {
    private final UserDao dao;

    public UserServiceImpl(UserDao dao) {
        this.dao = dao;
    }

    @Override
    public User getUser(long id) {
        return dao.find(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    @Transactional
    public User createUser(User user) {
        Optional<User> exist = dao.find(user.getId());
        if (exist.isPresent()) {
            throw new UserAlreadyExistsException(exist.get());
        }
        dao.create(user);
        return user;
    }

    @Override
    public User deleteUser(long id) {
        Optional<User> user = dao.find(id);
        if (user.isEmpty()) {
            throw new UserNotFoundException(id);
        }
        dao.delete(id);
        return user.get();
    }

    @Override
    public User updateUser(long id, User info) {
        Optional<User> user = dao.find(id);
        if (user.isEmpty()) {
            throw new UserNotFoundException(id);
        }
        User u = user.get();
        u.setName(info.getName());
        u.setProfile(info.getProfile());
        dao.update(u);
        return u;
    }
}
