package com.practice.dao.cassandra;


import com.practice.model.UserEvent;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserEventRepository extends CassandraRepository<UserEvent, UserEvent.UserEventKey> {
    List<UserEvent> findByKeyUserId(Long userId);
}