package com.practice.dao.sql;

import com.practice.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository("jdbcUserDao")
public class JdbcUserDao implements UserDao {
    private final JdbcTemplate jdbc;

    public JdbcUserDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final RowMapper<User> USER_ROW_MAPPER = new RowMapper<User>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new User(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("profile")
            );
        }
    };

    @Override
    public Optional<User> find(Long id) {
        List<User> list = jdbc.query("SELECT id, name, profile FROM users WHERE id = ?", USER_ROW_MAPPER, id);
        return list.stream().findFirst();
    }


    @Override
    public User save(User u) {
        jdbc.update("INSERT INTO users (username, password, role) VALUES (?, ?, ?)",
                    u.getUsername(), u.getPassword(), u.getRole());
        // get the generated id
        Long id = jdbc.queryForObject("SELECT currval(pg_get_serial_sequence('users', 'id'))", Long.class);
        u.setId(id);
        return u;
    }

    @Override
    public int update(User u) {
        return jdbc.update("UPDATE users SET name = ?, profile = ? WHERE id = ?",
                u.getUsername(), u.getPassword(), u.getId());
    }

    @Override
    public int delete(Long id) {
        return jdbc.update("DELETE FROM users WHERE id = ?", id);
    }

    @Override
    public Optional<User> findForUpdate(Long id) {
        return find(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        List<User> list = jdbc.query("SELECT id, username, password, role FROM users WHERE username = ?",
                USER_ROW_MAPPER, username);
        return list.stream().findFirst();
    }
}
