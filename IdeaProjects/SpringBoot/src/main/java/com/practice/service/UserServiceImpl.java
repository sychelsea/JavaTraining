package com.practice.service;

import com.practice.dao.sql.UserDao;
import com.practice.model.User;
import com.practice.exception.UserAlreadyExistsException;
import com.practice.exception.UserNotFoundException;
import com.practice.exception.UserOptimisticLockingFailureException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Qualifier("UserService")
public class UserServiceImpl implements UserService {
    private final UserDao dao;

    public UserServiceImpl(@Qualifier("jpaUserDao") UserDao dao) {
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
    @Transactional
    public User deleteUser(long id) {
        Optional<User> user = dao.find(id);
        if (user.isEmpty()) {
            throw new UserNotFoundException(id);
        }
        dao.delete(id);
        return user.get();
    }

    @Override
    @Transactional
    public User updateUser(long id, User info) {
        Optional<User> user = dao.find(id);
        if (user.isEmpty()) {
            throw new UserNotFoundException(id);
        }
        User u = user.get();
        if (info.getName() != null) {
            u.setName(info.getName());
        }
        if (info.getEmail() != null) {
            u.setEmail(info.getEmail());
        }
        dao.update(u);
        return u;
    }

    // For JPA implementation only
    @Transactional
    public User updateUserWithPessimisticLock(long id, User info, long holdMillis) {
        // pessimisticLock added in findForUpdate(id)
        User u = dao.findForUpdate(id).orElseThrow(() -> new UserNotFoundException(id));
        if (info.getName() != null)    u.setName(info.getName());
        if (info.getEmail() != null) u.setEmail(info.getEmail());

        // for observation
        if (holdMillis > 0) {
            try { Thread.sleep(holdMillis); } catch (InterruptedException ignored) {}
        }
        dao.update(u);
        return u;
    }

    // For JPA implementation only
    @Transactional
    public User updateUserWithOptimisticLock(long id, User info) {
        User u = dao.find(id).orElseThrow(() -> new UserNotFoundException(id));
        if (info.getName() != null)    u.setName(info.getName());
        if (info.getEmail() != null) u.setEmail(info.getEmail());
        try {
            // @Version => "WHERE id=? AND version=?"
            // Hibernate performs dirty checking at flush/commit time.
            dao.update(u);
            return u;
        } catch (org.springframework.orm.ObjectOptimisticLockingFailureException e) {
            throw new UserOptimisticLockingFailureException(u);
        }
    }
}
