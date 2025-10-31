package com.practice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {
    @Id
    @Column(name = "id")
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "profile")
    private String profile;

    public User() {} // for JSON/Jackson ONLY

    public User(long id) {
        this.id = id;
    }

    public User(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public User(long id, String name, String profile) {
        this.id = id;
        this.name = name;
        this.profile = profile;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getProfile() { return profile; }
    public void setProfile(String profile) { this.profile = profile; }
}
