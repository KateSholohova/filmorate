package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbc;
    private final NamedParameterJdbcOperations jdbcOperations;
    private final FilmRowMapper mapper;
    private final MpaDbStorage mpaDbStorage;
    private final GenreDbStorage genreDbStorage;

    public List<Film> findAll() {
        String query = "SELECT * FROM films";
        return jdbc.query(query, mapper);
    }

    public Film create(Film film) {
        log.info("Создание нового фильма: {}", film);
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(LocalDate.parse("1895-12-28"))) {
            log.error("Некорректная дата выхода: {}", film.getReleaseDate());
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        if(mpaDbStorage.findById(film.getMpa().getId()) == null){
            throw new NotFoundException("Mpa с id = " + film.getMpa().getId() + " не найден");
        }
        for (Genre genre : film.getGenres()) {
            if (genreDbStorage.findById(genre.getId()) == null) {
                throw new NotFoundException("Жанр с id = " + film.getMpa().getId() + " не найден");
            }
        }
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("name", film.getName());
        map.addValue("description", film.getDescription());
        map.addValue("releaseDate", film.getReleaseDate());
        map.addValue("duration", film.getDuration());
        map.addValue("mpa_id", film.getMpa().getId());
        //map.addValue("genre", film.getGenres());
        jdbcOperations.update(
                "INSERT INTO films(name, description, releaseDate, duration, mpa_id) VALUES (:name, :description, :releaseDate, :duration, :mpa_id)", map, keyHolder);

        log.info("Фильм {} сохранен", film);

        film.setId(keyHolder.getKey().longValue());


        return film;
    }

    public Film update(Film newFilm) {

        if (newFilm.getId() == null) {
            log.error("Нет id");
            throw new ValidationException("Id должен быть указан");
        }
        if (!jdbc.query("SELECT * FROM films WHERE ID = ?", mapper, newFilm.getId()).isEmpty()) {
            if (newFilm.getReleaseDate() != null && newFilm.getReleaseDate().isBefore(LocalDate.parse("1895-12-28"))) {
                log.error("Некорректная дата выхода: {}", newFilm.getReleaseDate());
                throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
            }

            String sql = "UPDATE FILMS SET NAME = ?, releaseDate = ?, DESCRIPTION = ?, MPA_ID = ?, DURATION = ? WHERE ID = ?";
            jdbc.update(sql, newFilm.getName(), newFilm.getReleaseDate(), newFilm.getDescription(), newFilm.getMpa().getId(), newFilm.getId());
            log.info("Фильм обновлен: {}", newFilm);
            return jdbc.queryForObject("SELECT * FROM films WHERE ID = ?", mapper, newFilm.getId());
        }
        log.error("Нет фильма с данным id: {}", newFilm.getId());
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    public void delete(long id) {
        if (!jdbc.query("SELECT * FROM films WHERE ID = ?", mapper, id).isEmpty()) {
            jdbc.update("DELETE FROM films WHERE ID = ?", id);
        }
    }

    public Film findById(long id) {
        if (jdbc.query("SELECT * FROM films WHERE ID = ?", mapper, id).isEmpty()) {
            return null;
        } else {
            return jdbc.query("SELECT * FROM films WHERE ID = ?", mapper, id).get(0);
        }
    }
}
