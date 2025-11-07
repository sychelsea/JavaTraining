package com.practice.service;

import com.practice.dao.cassandra.UserEventRepository;
import com.practice.dao.sql.UserDao;
import com.practice.model.User;
import com.practice.exception.UserAlreadyExistsException;
import com.practice.exception.UserNotFoundException;
import com.practice.exception.UserOptimisticLockingFailureException;
import com.practice.model.UserEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Qualifier("UserService")
public class UserServiceImpl implements UserService {
    private final UserDao dao;

    private final UserEventRepository eventRepo;

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(@Qualifier("jpaUserDao") UserDao dao,
                           @Autowired(required = false) UserEventRepository eventRepo, // false - Cassandra's failure won't affect the service
                           PasswordEncoder passwordEncoder) { // Inject Spring Security's PasswordEncoder
        this.dao = dao;
        this.eventRepo = eventRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Cacheable(cacheNames = "userById", key = "#id")
    public User getUser(long id) {
        return dao.find(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    @Transactional
    @CachePut(cacheNames = "userById", key = "#result.id")
    public User createUser(User user) {
        Optional<User> exist = dao.find(user.getId());
        if (exist.isPresent()) {
            throw new UserAlreadyExistsException(exist.get());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        dao.create(user);

        // write the event to Cassandra
        if (eventRepo != null)
            eventRepo.save(new UserEvent(user.getId(), "CREATE", "User created: " + user.getUsername()));

        return user;
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "userById", key = "#id")
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
    @CachePut(cacheNames = "userById", key = "#id")
    public User updateUser(long id, User info) {
        Optional<User> user = dao.find(id);
        if (user.isEmpty()) {
            throw new UserNotFoundException(id);
        }
        User u = user.get();
        if (info.getUsername() != null) {
            u.setUsername(info.getUsername());
        }
        if (info.getPassword() != null) {
            String encoded = passwordEncoder.encode(info.getPassword());
            u.setPassword(encoded);
        }
        if (info.getRole() != null) {
            u.setRole(info.getRole());
        }
        dao.update(u);

        if (eventRepo != null)
            eventRepo.save(new UserEvent(id, "UPDATE", "User updated: " + u.getUsername()));

        // write the event to Cassandra
        return u;
    }

    // For JPA implementation only
    @Override
    @Transactional
    @CachePut(cacheNames = "userById", key = "#id")
    public User updateUserWithPessimisticLock(long id, User info, long holdMillis) {
        // pessimisticLock added in findForUpdate(id)
        User u = dao.findForUpdate(id).orElseThrow(() -> new UserNotFoundException(id));
        if (info.getUsername() != null)    u.setUsername(info.getUsername());
        if (info.getPassword() != null) u.setPassword(info.getPassword());

        // for observation
        if (holdMillis > 0) {
            try { Thread.sleep(holdMillis); } catch (InterruptedException ignored) {}
        }
        dao.update(u);
        return u;
    }

    // For JPA implementation only
    @Override
    @Transactional
    @CachePut(cacheNames = "userById", key = "#id")
    public User updateUserWithOptimisticLock(long id, User info) {
        User u = dao.find(id).orElseThrow(() -> new UserNotFoundException(id));
        if (info.getUsername() != null)    u.setUsername(info.getUsername());
        if (info.getPassword() != null) u.setPassword(info.getPassword());
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
