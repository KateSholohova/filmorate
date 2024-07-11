package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;

import java.util.List;


@Slf4j
@Repository
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbc;
    private final GenreRowMapper mapper;

    public List<Genre> findAll() {
        String query = "SELECT * FROM genre";
        return jdbc.query(query, mapper);
    }

    public Genre findById(long id) {
        if (jdbc.query("SELECT * FROM genre WHERE ID = ?", mapper, id).isEmpty()) {
            throw new NotFoundException("Жанр с id = " + id + " не найден");
        }
        return jdbc.query("SELECT * FROM genre WHERE ID = ?", mapper, id).get(0);

    }
}
