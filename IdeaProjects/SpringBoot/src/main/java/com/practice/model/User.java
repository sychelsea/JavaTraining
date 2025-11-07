package com.practice.model;

import jakarta.persistence.*;

import java.io.Serializable;


// Implements Serializable so that User objects can be converted into a byte stream.
// The default RedisCacheManager uses JDK serialization to store cached values.

@Entity
@Table(name = "users")
public class User implements Serializable {

    // - A unique identifier used by Java during deserialization to verify
    //   that the sender and receiver of a serialized object have loaded
    //   classes that are compatible with respect to serialization.
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Version
    private Long version;

    @Column(name="role")
    private String role = "ROLE_USER";

    @Column(name="enabled")
    private boolean enabled = true;


    public User() {} // for JSON/Jackson ONLY

    public User(Long id) {
        this.id = id;
    }

    public User(Long id, String username) {
        this.id = id;
        this.username = username;
    }

    public User(Long id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String name) { this.username = name; }

    public String getPassword() { return password; }
    public void setPassword(String profile) { this.password = profile; }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
