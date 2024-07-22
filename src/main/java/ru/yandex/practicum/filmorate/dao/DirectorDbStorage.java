package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    private static final String SELECT_DIRECTORS = "SELECT d.director_id, d.name FROM directors AS d ";

    @Override
    public Director create(Director director) {
        String sql = "INSERT INTO directors (name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql, new String[]{"director_id"});
                    ps.setString(1, director.getName());
                    return ps;
                }, keyHolder);
        director.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return director;
    }

    @Override
    public Director update(Director director) {
        int id = director.getId();
        String sql = "UPDATE directors SET name = ? WHERE director_id = ?";
        jdbcTemplate.update(sql, director.getName(), id);
        return director;
    }

    @Override
    public List<Director> findAllDirectors() {
        String sql = "ORDER BY d.director_id";
        return jdbcTemplate.query(SELECT_DIRECTORS + sql, (rs, rowNum) -> makeDirector(rs));
    }

    @Override
    public Optional<Director> findDirectorById(int id) {
        String sql = "WHERE d.director_id = ?";
        return jdbcTemplate.query(SELECT_DIRECTORS + sql, (rs, rowNum) -> makeDirector(rs), id).stream().findFirst();
    }

    @Override
    public void removeDirectorById(int id) {
        String sql = "DELETE FROM directors WHERE director_id = ?";
        jdbcTemplate.update(sql, id);
    }

    private Director makeDirector(ResultSet rs) throws SQLException {
        int id = rs.getInt("director_id");
        return Director.builder()
                .id(id)
                .name(rs.getString("name"))
                .build();
    }
}
