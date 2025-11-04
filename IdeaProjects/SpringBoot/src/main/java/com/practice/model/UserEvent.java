package com.practice.model;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Table("user_events")
public class UserEvent implements Serializable {

    @PrimaryKeyClass
    public static class UserEventKey implements Serializable {
        @PrimaryKeyColumn(name = "user_id", type = PrimaryKeyType.PARTITIONED)
        private Long userId;

        @PrimaryKeyColumn(name = "event_time", type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
        private Instant eventTime;

        public UserEventKey() {}
        public UserEventKey(Long userId, Instant eventTime) {
            this.userId = userId;
            this.eventTime = eventTime;
        }
        public Long getUserId() { return userId; }
        public Instant getEventTime() { return eventTime; }
    }

    @PrimaryKey
    private UserEventKey key;

    @Column("event_type")
    private String eventType;

    @Column("payload")
    private String payload;

    @Column("event_id")
    private UUID eventId = UUID.randomUUID();

    public UserEvent() {}
    public UserEvent(Long userId, String type, String payload) {
        this.key = new UserEventKey(userId, Instant.now());
        this.eventType = type;
        this.payload = payload;
    }

    public UserEventKey getKey() {
        return key;
    }

    public void setKey(UserEventKey key) {
        this.key = key;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }
}

