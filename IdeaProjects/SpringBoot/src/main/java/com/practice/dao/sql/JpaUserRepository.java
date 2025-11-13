package com.practice.dao.sql;

import com.practice.model.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import jakarta.transaction.Transactional;

import java.util.Optional;

@Repository
public interface JpaUserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByUsername(String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM User u WHERE u.id = :id")
    User findByIdForUpdate(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("update User u set u.username = :username, u.password = :password, u.role = :role where u.id = :id")
    int updateUser(@Param("id") Long id,
                   @Param("username") String username,
                   @Param("password") String password,
                   @Param("role") String role);

    @Modifying
    @Transactional
    @Query("delete from User u where u.id = :id")
    int deleteByIdReturningCount(@Param("id") Long id);
}
