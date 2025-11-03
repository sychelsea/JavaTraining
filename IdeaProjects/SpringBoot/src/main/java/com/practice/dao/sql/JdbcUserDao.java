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
    public Optional<User> find(long id) {
        List<User> list = jdbc.query("SELECT id, name, profile FROM users WHERE id = ?", USER_ROW_MAPPER, id);
        return list.stream().findFirst();
    }


    @Override
    public int create(User u) {
        return jdbc.update("INSERT INTO users(id, name, profile) VALUES(?, ?, ?)",
                u.getId(), u.getName(), u.getEmail());
    }

    @Override
    public int update(User u) {
        return jdbc.update("UPDATE users SET name = ?, profile = ? WHERE id = ?",
                u.getName(), u.getEmail(), u.getId());
    }

    @Override
    public int delete(long id) {
        return jdbc.update("DELETE FROM users WHERE id = ?", id);
    }

    @Override
    public Optional<User> findForUpdate(long id) {
        return find(id);
    }
}
