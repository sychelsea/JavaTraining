package com.practice.dao;

import com.practice.domain.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("hibernateUserDao")
public class HibernateUserDao implements UserDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<User> find(long id) {
        return Optional.ofNullable(em.find(User.class, id));
    }

    @Override
    public int create(User u) {
        em.persist(u);
        return 1; // 1 - created
    }

    @Override
    public int update(User u) {
        int rows = em.createQuery(
                        "UPDATE User u SET u.name=:name, u.profile=:profile WHERE u.id=:id")
                .setParameter("name", u.getName())
                .setParameter("profile", u.getProfile())
                .setParameter("id", u.getId())
                .executeUpdate();
        return rows; // 0
    }

    @Override
    public int delete(long id) {
        int rows = em.createQuery("DELETE FROM User u WHERE u.id=:id")
                .setParameter("id", id)
                .executeUpdate();
        return rows;
    }
}
