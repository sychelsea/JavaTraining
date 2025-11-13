package com.practice.dao.sql;

import com.practice.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository("hibernateUserDao")
public class HibernateUserDao implements UserDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<User> find(Long id) {
        return Optional.ofNullable(em.find(User.class, id));
    }

    @Override
    @Transactional
    public User save(User u) {
        em.persist(u);
        return u;
    }

    @Override
    @Transactional
    public int update(User u) {
        int rows = em.createQuery(
                        "UPDATE User u SET u.username=:name, u.password=:profile WHERE u.id=:id")
                .setParameter("name", u.getUsername())
                .setParameter("profile", u.getPassword())
                .setParameter("id", u.getId())
                .executeUpdate();
        return rows; // 0
    }

    @Override
    public int delete(Long id) {
        int rows = em.createQuery("DELETE FROM User u WHERE u.id=:id")
                .setParameter("id", id)
                .executeUpdate();
        return rows;
    }

    @Override
    @Transactional
    public Optional<User> findForUpdate(Long id) {
        return find(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                .setParameter("username", username)
                .getResultStream()
                .findFirst();
    }
}
