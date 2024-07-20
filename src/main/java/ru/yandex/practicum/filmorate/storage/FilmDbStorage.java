package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
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
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;

import java.sql.PreparedStatement;
import java.sql.SQLException;
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
    private final GenreRowMapper genreRowMapper;

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
        if ((film.getMpa().getId()) > 5) {
            throw new ValidationException("Mpa неверный");
        }
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                if ((genre.getId()) > 6) {
                    throw new ValidationException("Жанр неверный");
                }
            }
        }


        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("name", film.getName());
        map.addValue("description", film.getDescription());
        map.addValue("releaseDate", film.getReleaseDate());
        map.addValue("duration", film.getDuration());
        map.addValue("mpa_id", film.getMpa().getId());
        jdbcOperations.update(
                "INSERT INTO films(name, description, releaseDate, duration, mpa_id) VALUES (:name, :description, :releaseDate, :duration, :mpa_id)", map, keyHolder);

        log.info("Фильм {} сохранен", film);

        film.setId(keyHolder.getKey().longValue());

        if (film.getGenres() != null) {
            List<Genre> genreList = film.getGenres();
            jdbc.batchUpdate("INSERT INTO films_genre(film_id, genre_id) VALUES(?, ?)", new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                    preparedStatement.setLong(1, film.getId());
                    preparedStatement.setLong(2, genreList.get(i).getId());
                }

                @Override
                public int getBatchSize() {
                    return genreList.size();
                }
            });
        }


        return film;
    }

    public Film update(Film newFilm) {

        if (newFilm.getId() == null) {
            log.error("Нет id");
            throw new ValidationException("Id должен быть указан");
        }
        if (jdbc.query("SELECT * FROM films WHERE ID = ?", mapper, newFilm.getId()).isEmpty()) {
            log.error("Нет фильма с данным id: {}", newFilm.getId());
            throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");

        }

        if (newFilm.getReleaseDate() != null && newFilm.getReleaseDate().isBefore(LocalDate.parse("1895-12-28"))) {
            log.error("Некорректная дата выхода: {}", newFilm.getReleaseDate());
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        System.out.println(newFilm.getId());
        String sql = "UPDATE FILMS SET NAME = ?, releaseDate = ?, DESCRIPTION = ?, MPA_ID = ?, DURATION = ? WHERE ID = ?";

        jdbc.update(sql, newFilm.getName(), newFilm.getReleaseDate(), newFilm.getDescription(), newFilm.getMpa().getId(), newFilm.getDuration(), newFilm.getId());
        jdbc.update("DELETE FROM films_genre WHERE film_id = ?", newFilm.getId());
        if (newFilm.getGenres() != null) {
            List<Genre> genreList = newFilm.getGenres();
            jdbc.batchUpdate("INSERT INTO films_genre(film_id, genre_id) VALUES(?, ?)", new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                    preparedStatement.setLong(1, newFilm.getId());
                    preparedStatement.setLong(2, genreList.get(i).getId());
                }

                @Override
                public int getBatchSize() {
                    return genreList.size();
                }
            });
        }

        log.info("Фильм обновлен: {}", newFilm);
        return jdbc.queryForObject("SELECT * FROM films WHERE ID = ?", mapper, newFilm.getId());

    }

    public void delete(long id) {
        if (!jdbc.query("SELECT * FROM films WHERE ID = ?", mapper, id).isEmpty()) {
            jdbc.update("DELETE FROM films WHERE ID = ?", id);
        }
    }

    public Film findById(long id) {

        final List<Film> films = jdbc.query("SELECT * FROM films WHERE ID = ?", mapper, id);
        if (films.size() != 1) {
            throw new NotFoundException("film id=" + id);
        }
        Film film = films.get(0);
        film.setMpa(mpaDbStorage.findById(film.getMpa().getId()));
        String sql = "SELECT G.* FROM films_genre AS F JOIN genre AS G ON F.genre_id = G.id WHERE F.film_id = ? GROUP BY G.id";
        List<Genre> genreList = jdbc.query(sql, genreRowMapper, id);
        film.setGenres(genreList);
        return film;

    }
}
