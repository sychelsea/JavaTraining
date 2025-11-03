package com.practice.dao.sql;

import com.practice.domain.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import jakarta.transaction.Transactional;

@Repository
public interface JpaUserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM User u WHERE u.id = :id")
    User findByIdForUpdate(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("update User u set u.name = :name, u.profile = :profile where u.id = :id")
    int updateUser(@Param("id") long id,
                   @Param("name") String name,
                   @Param("profile") String profile);

    @Modifying
    @Transactional
    @Query("delete from User u where u.id = :id")
    int deleteByIdReturningCount(@Param("id") long id);
}
