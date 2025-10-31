package com.practice.dao;

import com.practice.domain.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository("jpaUserDao")
public class JpaUserDao implements UserDao {

    private final JpaUserRepository repo;

    public JpaUserDao(JpaUserRepository repo) {
        this.repo = repo;
    }

    @Override
    public Optional<User> find(long id) {
        return repo.findById(id);
    }

    @Override
    @Transactional
    public int create(User u) {
        repo.saveAndFlush(u);
        return 1;
    }

    @Override
    @Transactional
    public int update(User u) {
        return repo.updateUser(u.getId(), u.getName(), u.getProfile());
    }

    @Override
    @Transactional
    public int delete(long id) {
        return repo.deleteByIdReturningCount(id);
    }
}
