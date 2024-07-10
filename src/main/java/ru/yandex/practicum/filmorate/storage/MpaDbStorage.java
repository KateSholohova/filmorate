package ru.yandex.practicum.filmorate.storage;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mappers.MpaRowMapper;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbc;
    private final MpaRowMapper mapper;

    public List<Mpa> findAll() {
        String query = "SELECT * FROM mpa";
        return jdbc.query(query, mapper);
    }

    public Mpa findById(long id) {
        if (jdbc.query("SELECT * FROM mpa WHERE ID = ?", mapper, id).isEmpty()) {
            throw new NotFoundException("Mpa с id = " + id + " не найден");
        } else {
            return jdbc.query("SELECT * FROM mpa WHERE ID = ?", mapper, id).get(0);
        }
    }

}
