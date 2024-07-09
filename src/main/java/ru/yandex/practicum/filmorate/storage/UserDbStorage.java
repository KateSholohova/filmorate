package ru.yandex.practicum.filmorate.storage;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbc;
    private final UserRowMapper mapper;

    public List<User> findAll() {
        String query = "SELECT * FROM users";
        return jdbc.query(query, mapper);
    }

    public User create(User user) {
        log.info("Создание нового пользователя: {}", user);
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Имя не указано. Используем логин в качестве имени: {}", user.getLogin());
            user.setName(user.getLogin());
        }
        jdbc.update(
                "INSERT INTO users VALUES (?, ?, ?, ?, ?)",
                user.getId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());

        log.info("Пользователь {} сохранен", user);

        return jdbc.queryForObject("SELECT * FROM users WHERE ID = ?", mapper, user.getId());
    }

    public User update(User newUser) {

        if (newUser.getId() == null) {
            log.error("Нет id");
            throw new ValidationException("Id должен быть указан");
        }
        if (!jdbc.query("SELECT * FROM users WHERE ID = ?", mapper, newUser.getId()).isEmpty()) {
            String sql = "UPDATE USERS SET EMAIL = ?, NAME = ?, LOGIN = ?, BIRTHDAY = ? WHERE ID = ?";
            jdbc.update(sql, newUser.getEmail(), newUser.getName(), newUser.getLogin(), newUser.getBirthday(), newUser.getId());
            return jdbc.queryForObject("SELECT * FROM users WHERE ID = ?", mapper, newUser.getId());
        }
        log.error("Нет пользователя с данным id: {}", newUser.getId());
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }

    public void delete(long id) {
        if (!jdbc.query("SELECT * FROM users WHERE ID = ?", mapper, id).isEmpty()) {
            jdbc.update("DELETE FROM users WHERE ID = ?", id);
        }
    }

    public User findById(long id) {
        if (jdbc.query("SELECT * FROM users WHERE ID = ?", mapper, id).isEmpty()) {
            return null;
        } else {
            return jdbc.query("SELECT * FROM users WHERE ID = ?", mapper, id).get(0);
        }
    }


}

