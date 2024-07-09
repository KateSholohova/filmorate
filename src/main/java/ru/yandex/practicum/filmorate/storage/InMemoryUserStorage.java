package ru.yandex.practicum.filmorate.storage;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Data
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private long count = 0L;

    @Override
    public User create(User user) {
        log.info("Создание нового пользователя: {}", user);
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Имя не указано. Используем логин в качестве имени: {}", user.getLogin());
            user.setName(user.getLogin());
        }
        user.setId(identify());
        users.put(user.getId(), user);
        log.info("Пользователь создан: {}", user);
        return user;
    }

    @Override
    public User update(User newUser) {

        if (newUser.getId() == null) {
            log.error("Нет id");
            throw new ValidationException("Id должен быть указан");
        }
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            if (newUser.getName() != null) {
                oldUser.setName(newUser.getName());
            }
            if (newUser.getBirthday() != null) {
                oldUser.setBirthday(newUser.getBirthday());
            }
            if (newUser.getEmail() != null) {
                oldUser.setEmail(newUser.getEmail());
            }
            if (newUser.getLogin() != null) {
                oldUser.setLogin(newUser.getLogin());
            }
            log.info("Пользователь обновлен: {}", oldUser);
            return oldUser;
        }
        log.error("Нет пользователя с данным id: {}", newUser.getId());
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }

    @Override
    public void delete(long id) {
        if (users.containsKey(id)) {
            users.remove(id);
        }
    }

    public Collection<User> findAll() {
        return users.values();
    }

    public User findById(long id) {
        return users.get(id);
    }

    public long identify() {
        return ++count;
    }
}
