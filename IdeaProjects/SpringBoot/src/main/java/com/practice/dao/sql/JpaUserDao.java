package com.practice.dao.sql;

import com.practice.model.User;
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
    public Optional<User> find(Long id) {
        return repo.findById(id);
    }

    @Override
    @Transactional
    public Optional<User> findForUpdate(Long id) {
        User u = repo.findByIdForUpdate(id);
        return Optional.ofNullable(u);
    }

    @Override public Optional<User> findByUsername(String username) {
        return repo.findByUsername(username);
    }

    @Override
    @Transactional
    public User save(User u) {
        return repo.saveAndFlush(u);
    }

    @Override
    @Transactional
    public int update(User u) {
        return repo.updateUser(u.getId(), u.getUsername(), u.getPassword(), u.getRole());
    }

    @Override
    @Transactional
    public int delete(Long id) {
        return repo.deleteByIdReturningCount(id);
    }


}
