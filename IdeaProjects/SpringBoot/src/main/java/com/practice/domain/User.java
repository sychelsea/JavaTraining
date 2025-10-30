package com.practice.domain;

public class User {
    private long id;
    private String name;
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
